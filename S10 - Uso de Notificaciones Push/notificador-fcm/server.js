const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const admin = require('firebase-admin');

// Inicializa Firebase Admin con el archivo de credenciales
const serviceAccount = require('./serviceAccountKey.json'); // Tu archivo JSON de la cuenta de servicio
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const app = express();
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public')); // Para servir la página HTML

// Endpoint para enviar notificaciones individuales (token específico)
app.post('/api/enviar-notificacion', async (req, res) => {
  try {
    const { token, titulo, mensaje } = req.body;
    
    if (!token || !titulo || !mensaje) {
      return res.status(400).json({ error: 'Token, título y mensaje son obligatorios' });
    }

    // Enviar mensaje usando Admin SDK
    const mensaje_respuesta = await admin.messaging().send({
      token: token,
      notification: {
        title: titulo,
        body: mensaje
      },
      data: {
        tipo: 'notificacion',
        mensaje: mensaje
      }
    });

    // Guardar registro en Firestore
    await admin.firestore().collection('notificaciones').add({
      token: token,
      asunto: titulo,
      mensaje: mensaje,
      estado: 'enviado',
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    res.status(200).json({ success: true, result: mensaje_respuesta });
  } catch (error) {
    console.error('Error al enviar notificación:', error);
    res.status(500).json({ error: error.message });
  }
});

// Nuevo endpoint para enviar notificaciones a grupos (topics)
app.post('/api/enviar-a-grupo', async (req, res) => {
  try {
    const { grupo, titulo, mensaje } = req.body;
    
    if (!grupo || !titulo || !mensaje) {
      return res.status(400).json({ error: 'Grupo, título y mensaje son obligatorios' });
    }

    // Enviar mensaje a un tema (topic) usando Admin SDK
    const mensaje_respuesta = await admin.messaging().send({
      topic: grupo,
      notification: {
        title: titulo,
        body: mensaje
      },
      data: {
        tipo: 'notificacion_grupo',
        mensaje: mensaje
      }
    });

    // Guardar registro en Firestore
    await admin.firestore().collection('notificaciones_grupo').add({
      grupo: grupo,
      asunto: titulo,
      mensaje: mensaje,
      estado: 'enviado',
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });

    res.status(200).json({ success: true, result: mensaje_respuesta });
  } catch (error) {
    console.error('Error al enviar notificación a grupo:', error);
    res.status(500).json({ error: error.message });
  }
});

// Endpoint para obtener lista de grupos disponibles
app.get('/api/grupos', async (req, res) => {
  try {
    // Estos podrían venir desde Firestore en una implementación real
    const grupos = [
      { id: 'general', nombre: 'General (Todos los usuarios)' },
      { id: 'marketing', nombre: 'Marketing' },
      { id: 'noticias', nombre: 'Noticias' },
      { id: 'alertas', nombre: 'Alertas' },
      { id: 'actualizaciones', nombre: 'Actualizaciones' }
    ];
    
    res.status(200).json({ grupos });
  } catch (error) {
    console.error('Error al obtener grupos:', error);
    res.status(500).json({ error: error.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor escuchando en puerto ${PORT}`);
});