<?php
require_once '../../config/conexion.php';

header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];

if ($metodo == 'GET') {
    try {
        $conn = conectarDB();
        
        if (isset($_GET['id'])) {
            $id = $_GET['id'];
            $stmt = $conn->prepare("SELECT * FROM contactos WHERE id_contacto = ?");
            $stmt->execute([$id]);
            $contacto = $stmt->fetch(PDO::FETCH_ASSOC);
            
            if ($contacto) {
                echo json_encode($contacto);
            } else {
                http_response_code(404);
                echo json_encode(['mensaje' => 'Contacto no encontrado']);
            }
        } else {

            $stmt = $conn->query("SELECT * FROM contactos");
            $contactos = $stmt->fetchAll(PDO::FETCH_ASSOC);
            echo json_encode($contactos);
        }
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Error al obtener contactos: ' . $e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Método no permitido']);
}
