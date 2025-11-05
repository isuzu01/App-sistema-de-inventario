package com.example.inventarioapp.repository

import android.util.Log
import com.example.inventarioapp.dao.ProveedorDao
import com.example.inventarioapp.entity.ProveedorEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseProveedorRepository {
    private const val TAG = "FirebaseProveedorRepo"
    private var isSyncing = false
    private val dbRef = FirebaseDatabase.getInstance().getReference("proveedores")

    suspend fun insetarProveedorFirebaseYRoom(
        proveedorDao: ProveedorDao,
        proveedor: ProveedorEntity
    ) {
        withContext(Dispatchers.IO) {
            try {
                val newId = proveedorDao.insertProveedor(proveedor)
                val proveedorConId = proveedor.copy(id = newId)
                dbRef.child(newId.toString()).setValue(proveedorConId).await()

                Log.d(TAG, "Proveedor guardao - Room ID $newId, Firebasse ID: $newId")
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar proveedor: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun actualizarProveedorFirabaseYRoom(
        proveedorDao: ProveedorDao,
        proveedor: ProveedorEntity
    ) {
        withContext(Dispatchers.IO) {
            try {
                proveedorDao.updateProveedor(proveedor)
                dbRef.child(proveedor.id.toString()).setValue(proveedor).await()

                Log.d(TAG, "Proveedor actualizado - ID: ${proveedor.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error al actualizar proveedor: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun eliminarProveedorFirebaseYRoom(
        proveedorDao: ProveedorDao,
        proveedor: ProveedorEntity
    ) {
        withContext(Dispatchers.IO) {
            try {
                dbRef.child(proveedor.id.toString()).removeValue().await()
                proveedorDao.deleteProveedor(proveedor)

                Log.d(TAG, "Proveedor eliminado - ID ${proveedor.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error al eliminar proveedor: ${e.message}", e)
                throw e
            }
        }
    }

    fun sincronizarProveedorDesdeFirebase(proveedorDao: ProveedorDao) {
        if (isSyncing) return

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isSyncing) return
                isSyncing = true

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val proveedoresFirebase = mutableListOf<ProveedorEntity>()
                        val proveedoresLocales = proveedorDao.getAllProveedores()

                        for (item in snapshot.children) {
                            try {
                                val proveedor = item.getValue(ProveedorEntity::class.java)
                                val idFromKey = item.key?.toLongOrNull()

                                if (proveedor != null && idFromKey != null) {
                                    proveedoresFirebase.add(proveedor.copy(id = idFromKey))
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error procesando producto Firebase: ${e.message}")
                            }
                        }

                        for (local in proveedoresLocales) {
                            if (proveedoresFirebase.none { it.id == local.id }) {
                                proveedorDao.deleteProveedor(local)
                            }
                        }

                        for (firebaseProveedores in proveedoresFirebase) {
                            try {
                                val proveedorExistente =
                                    proveedorDao.getProveedorById(firebaseProveedores.id)

                                if (proveedorExistente == null) {
                                    proveedorDao.insertProveedor(firebaseProveedores)
                                } else {
                                    if (proveedorExistente != firebaseProveedores) {
                                        proveedorDao.updateProveedor(firebaseProveedores)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "Error sincronizando producto ID ${firebaseProveedores.id}: ${e.message}"
                                )
                            }
                        }
                        Log.d(
                            TAG,
                            "Sincronizacion completa. Proveedores: ${proveedoresFirebase.size}"
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
                Log.e(TAG, "Error en sincronización: ${error.message}")
            }
        })
    }

    fun observarProveedoresEnTiempoReal(callback: (List<ProveedorEntity>) -> Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val proveedores = mutableListOf<ProveedorEntity>()
                for (item in snapshot.children) {
                    try {
                        val proveedor = item.getValue(ProveedorEntity::class.java)
                        val idFromKey = item.key?.toLongOrNull()

                        if (proveedor != null && idFromKey != null) {
                            proveedores.add(proveedor.copy(id = idFromKey))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error procesando producto Firebase: ${e.message}")
                    }
                }
                callback(proveedores)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error en sincronización: ${error.message}")
                callback(emptyList())
            }
        })
    }
}