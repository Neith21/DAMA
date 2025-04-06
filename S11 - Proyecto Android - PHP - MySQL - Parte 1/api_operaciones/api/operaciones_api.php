<?php
require_once '../config/config.php';
require_once '../funciones/operaciones.php';
require_once '../utils/error.php';

header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true);

if (isset($input['accion'], $input['valor1'], $input['valor2'])) {
    $accion = $input['accion'];
    $valor1 = $input['valor1'];
    $valor2 = $input['valor2'];

    $resultado = realizarOperacion($accion, $valor1, $valor2);
    
    if (isset($resultado['error'])) {
        echo json_encode($resultado);
    } else {
        $response = [
            'accion' => $accion,
            'valor1' => $valor1,
            'valor2' => $valor2,
            'resultado' => $resultado
        ];
        echo json_encode($response);
    }
} else {
    mostrarError('Faltan parámetros.');
}
?>