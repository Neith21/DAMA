<?php
require_once '../../config/conexion.php';

header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];

if ($metodo == 'PUT') {
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
        
        $campos = [];
        $valores = [];
        
        if (isset($datos['nombre'])) {
            $campos[] = "nombre = ?";
            $valores[] = $datos['nombre'];
        }
        
        if (isset($datos['telefono'])) {
            $campos[] = "telefono = ?";
            $valores[] = $datos['telefono'];
        }
        
        if (empty($campos)) {
            http_response_code(400);
            echo json_encode(['error' => 'No hay datos para actualizar']);
            exit;
        }
        
        $sql = "UPDATE contactos SET " . implode(", ", $campos) . " WHERE id_contacto = ?";
        $valores[] = $id;
        
        $stmt = $conn->prepare($sql);
        $stmt->execute($valores);
        
        $stmt = $conn->prepare("SELECT * FROM contactos WHERE id_contacto = ?");
        $stmt->execute([$id]);
        $contacto = $stmt->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'mensaje' => 'Contacto actualizado con éxito',
            'contacto' => $contacto
        ]);
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Error al actualizar contacto: ' . $e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Método no permitido']);
}
