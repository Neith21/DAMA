<?php
require_once '../../config/conexion.php';

header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];

if ($metodo == 'DELETE') {
    try {
        $datos = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($datos['id_contacto'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Se requiere el ID del contacto']);
            exit;
        }
        
        $id = $datos['id_contacto'];
        
        $conn = conectarDB();

        $stmt = $conn->prepare("SELECT * FROM contactos WHERE id_contacto = ?");
        $stmt->execute([$id]);
        if ($stmt->rowCount() == 0) {
            http_response_code(404);
            echo json_encode(['error' => 'Contacto no encontrado']);
            exit;
        }

        $stmt = $conn->prepare("DELETE FROM contactos WHERE id_contacto = ?");
        $stmt->execute([$id]);
        
        echo json_encode(['mensaje' => 'Contacto eliminado con éxito']);
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Error al eliminar contacto: ' . $e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Método no permitido']);
}
