package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.dao.ProductoDao
import com.example.inventarioapp.entity.ProductoEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseProductoRepository {

    private const val TAG = "FirebaseProductoRepo"
    private var isSyncing = false

    suspend fun insertarProductoFirebaseYRoom(productoDao: ProductoDao, producto: ProductoEntity) {
        withContext(Dispatchers.IO) {

            try {
                val newId = productoDao.insertProducto(producto)
                val productoConId = producto.copy(id = newId)

                val dbRef = FirebaseDatabase.getInstance().getReference("productos")
                dbRef.child(newId.toString()).setValue(productoConId).await()

                Log.d(TAG, "Producto guardado - Room ID: $newId, Firebase ID: $newId")
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar producto: ${e.message}", e)
                throw e
            }

        }
    }

    suspend fun actualizarProductoFirebaseYRoom(
        productoDao: ProductoDao,
        producto: ProductoEntity
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Actualizar en Room
                productoDao.updateProducto(producto)

                // Actualizar en Firebase
                val dbRef = FirebaseDatabase.getInstance().getReference("productos")
                dbRef.child(producto.id.toString()).setValue(producto).await()

                Log.d(TAG, "Producto actualizado - ID: ${producto.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar producto: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun eliminarProductoFirebaseYRoom(productoDao: ProductoDao, producto: ProductoEntity) {
        withContext(Dispatchers.IO) {
            try {
                // Eliminar de Firebase
                val dbRef = FirebaseDatabase.getInstance().getReference("productos")
                dbRef.child(producto.id.toString()).removeValue().await()

                // Eliminar de Room
                productoDao.deleteProducto(producto)

                Log.d(TAG, "Producto eliminado - ID: ${producto.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar producto: ${e.message}", e)
                throw e
            }
        }
    }

    fun sincronizarProductosDesdeFirebase(productoDao: ProductoDao) {
        if (isSyncing) return
        val dbRef = FirebaseDatabase.getInstance().getReference("productos")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isSyncing) return
                isSyncing = true

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val productosFirebase = mutableListOf<ProductoEntity>()

                        // Recopilar productos de Firebase
                        for (item in snapshot.children) {
                            try {
                                val producto = item.getValue(ProductoEntity::class.java)
                                val idFromKey = item.key?.toLongOrNull()

                                if (producto != null && idFromKey != null) {
                                    productosFirebase.add(producto.copy(id = idFromKey))
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error procesando producto Firebase: ${e.message}")
                            }
                        }

                        // Obtener productos locales actuales
                        val productosLocales = productoDao.getAllProductos()

                        // Sincronizar: eliminar locales que no están en Firebase
                        for (local in productosLocales) {
                            if (productosFirebase.none { it.id == local.id }) {
                                productoDao.deleteProducto(local)
                            }
                        }

                        // Insertar o actualizar productos de Firebase
                        for (firebaseProducto in productosFirebase) {
                            try {
                                val productoExistente =
                                    productoDao.getProductoById(firebaseProducto.id)

                                if (productoExistente == null) {
                                    // Insertar nuevo producto
                                    productoDao.insertProducto(firebaseProducto)
                                } else {
                                    // Actualizar producto existente solo si es diferente
                                    if (productoExistente != firebaseProducto) {
                                        productoDao.updateProducto(firebaseProducto)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "Error sincronizando producto ID ${firebaseProducto.id}: ${e.message}"
                                )
                            }
                        }

                        Log.d(
                            TAG,
                            "Sincronización completada. Productos: ${productosFirebase.size}"
                        )

                    } catch (e: Exception) {
                        Log.e(TAG, "Error en sincronización: ${e.message}", e)
                    } finally {
                        isSyncing = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isSyncing = false
                Log.e(TAG, "Error en sincronización Firebase: ${error.message}")
            }
        })
    }


    // Función para observar cambios en tiempo real (solo lectura)
    fun observarProductosEnTiempoReal(callback: (List<ProductoEntity>) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("productos")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productos = mutableListOf<ProductoEntity>()
                for (item in snapshot.children) {
                    try {
                        val producto = item.getValue(ProductoEntity::class.java)
                        val idFromKey = item.key?.toLongOrNull()

                        if (producto != null && idFromKey != null) {
                            productos.add(producto.copy(id = idFromKey))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando producto: ${e.message}")
                    }
                }
                callback(productos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error observando productos: ${error.message}")
                callback(emptyList())
            }
        })
    }

}
