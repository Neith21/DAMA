package com.cchavez.notificaciones_push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log


class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(mensajeRemoto: RemoteMessage) {
        super.onMessageReceived(mensajeRemoto)
        mensajeRemoto.notification?.let {
            Log.d("FCM", "Mensaje recibido: Título=${it.title}, Cuerpo=${it.body}")
            mostrarNotificacion(it.title ?: "Título", it.body ?: "Cuerpo")
        }

        mensajeRemoto.data.isNotEmpty().let {
            val tipo = mensajeRemoto.data["tipo"]
            val mensaje = mensajeRemoto.data["mensaje"]
            Log.d("FCM","Datos recibidos: tipo=$tipo, mensaje=$mensaje")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM","Nuevo token: $token")
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val idCanal = "canal_predeterminado"
        val idNotificacion = 1

        val administradorNotificaciones = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                idCanal,
                "Canal Predeterminado",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            administradorNotificaciones.createNotificationChannel(canal)
        }

        val notificacion = NotificationCompat.Builder(this, idCanal)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        administradorNotificaciones.notify(idNotificacion, notificacion)
    }

}
