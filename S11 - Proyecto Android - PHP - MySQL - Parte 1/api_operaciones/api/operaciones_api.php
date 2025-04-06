<?php
header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true);

if (isset($input['accion'], $input['valor1'], $input['valor2'])) {
    $accion = $input['accion'];
    $valor1 = $input['valor1'];
    $valor2 = $input['valor2'];

    if ($accion === 'suma') {
        $resultado = $valor1 + $valor2;

        $response = [
            'accion' => $accion,
            'valor1' => $valor1,
            'valor2' => $valor2,
            'resultado' => $resultado
        ];

        echo json_encode($response);
    } else {
        echo json_encode(['error' => 'Acción no soportada.']);
    }
} else {
    echo json_encode(['error' => 'Faltan parámetros.']);
}
?>
