<?php
// Configuración de cabeceras para API
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Manejo de solicitudes OPTIONS (para CORS)
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    header("HTTP/1.1 200 OK");
    exit;
}

// Control de errores
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Incluir archivos necesarios
require_once '../config/Conexion.php';
require_once '../models/Contacto.php';

// Instanciar el modelo
$contactoModel = new Contacto();

// Capturar la acción solicitada
$action = isset($_GET['action']) ? $_GET['action'] : '';

try {
    switch ($action) {
        // SELECT: Obtener todos los contactos
        case 'listarContactos':
            $contactos = $contactoModel->listarContactos();
            echo json_encode([
                'status' => 'success',
                'data' => $contactos,
                'total_rows' => count($contactos)
            ]);
            break;

        // SELECT: Obtener un contacto por ID
        case 'listarContactoById':
            $id_contacto = isset($_GET['id']) ? $_GET['id'] : 0;
            $contacto = $contactoModel->listarContactoPorId($id_contacto);
            if ($contacto) {
                echo json_encode([
                    'status' => 'success',
                    'data' => $contacto
                ]);
            } else {
                echo json_encode([
                    'status' => 'error',
                    'message' => 'Contacto no encontrado.'
                ]);
            }
            break;

        // SELECT: Obtener contactos con filtro
        case 'listarContactosConFiltro':
            $filtro = isset($_GET['filtro']) ? $_GET['filtro'] : '';
            $contactos = $contactoModel->listarContactosConFiltro($filtro);
            echo json_encode([
                'status' => 'success',
                'data' => $contactos,
                'total_rows' => count($contactos)
            ]);
            break;

        // INSERT: Agregar un nuevo contacto
        case 'agregarContacto':
            if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
                throw new Exception("Método no permitido. Use POST.");
            }
            $data = json_decode(file_get_contents("php://input"), true);
            
            if (!is_array($data)) {
                throw new Exception("Datos inválidos. Se esperaba un JSON.");
            }
            
            $nombre = isset($data['nombre']) ? $data['nombre'] : '';
            $telefono = isset($data['telefono']) ? $data['telefono'] : '';
            
            $id_contacto = $contactoModel->insertarContacto($nombre, $telefono);
            echo json_encode([
                'status' => 'success',
                'message' => 'Contacto insertado con éxito.',
                'id_contacto' => $id_contacto
            ]);
            break;

        // UPDATE: Actualizar un contacto existente
        case 'actualizarContacto':
            if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
                throw new Exception("Método no permitido. Use POST.");
            }
            $data = json_decode(file_get_contents("php://input"), true);
            
            if (!is_array($data)) {
                throw new Exception("Datos inválidos. Se esperaba un JSON.");
            }
            
            $id_contacto = isset($data['id_contacto']) ? $data['id_contacto'] : 0;
            $nombre = isset($data['nombre']) ? $data['nombre'] : '';
            $telefono = isset($data['telefono']) ? $data['telefono'] : '';
            
            $filasAfectadas = $contactoModel->actualizarContacto($id_contacto, $nombre, $telefono);
            if ($filasAfectadas > 0) {
                echo json_encode([
                    'status' => 'success',
                    'message' => 'Contacto actualizado con éxito.'
                ]);
            } else {
                echo json_encode([
                    'status' => 'error',
                    'message' => 'No se encontró el contacto o no se realizaron cambios.'
                ]);
            }
            break;

        // DELETE: Eliminar un contacto
        case 'eliminarContacto':
            if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
                throw new Exception("Método no permitido. Use POST.");
            }
            $data = json_decode(file_get_contents("php://input"), true);
            
            if (!is_array($data)) {
                throw new Exception("Datos inválidos. Se esperaba un JSON.");
            }
            
            $id_contacto = isset($data['id_contacto']) ? $data['id_contacto'] : 0;
            
            $filasAfectadas = $contactoModel->eliminarContacto($id_contacto);
            if ($filasAfectadas > 0) {
                echo json_encode([
                    'status' => 'success',
                    'message' => 'Contacto eliminado con éxito.'
                ]);
            } else {
                echo json_encode([
                    'status' => 'error',
                    'message' => 'No se encontró el contacto.'
                ]);
            }
            break;
            
        default:
            echo json_encode([
                'status' => 'error',
                'message' => 'Acción no reconocida.'
            ]);
            break;
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}