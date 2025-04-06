<?php
require_once '../../config/conexion.php';

header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];

if ($metodo == 'POST') {
    try {
        $datos = json_decode(file_get_contents('php://input'), true);
        
        if (!isset($datos['nombre']) || !isset($datos['telefono'])) {
            http_response_code(400);
            echo json_encode(['error' => 'Faltan datos requeridos (nombre y telefono)']);
            exit;
        }
        
        $nombre = $datos['nombre'];
        $telefono = $datos['telefono'];
        
        $conn = conectarDB();
        $stmt = $conn->prepare("INSERT INTO contactos (nombre, telefono) VALUES (?, ?)");
        $stmt->execute([$nombre, $telefono]);
        
        $id = $conn->lastInsertId();
        
        http_response_code(201);
        echo json_encode([
            'mensaje' => 'Contacto creado con éxito',
            'id_contacto' => $id,
            'nombre' => $nombre,
            'telefono' => $telefono
        ]);
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Error al crear contacto: ' . $e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Método no permitido']);
}
