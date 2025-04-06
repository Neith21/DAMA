<?php
require_once '../utils/error.php';

header('Content-Type: application/json');

$input = json_decode(file_get_contents('php://input'), true);

if (isset($input['monto_bruto'])) {
    $monto_bruto = floatval($input['monto_bruto']);
    
    $iva = $monto_bruto * 0.13;
    $renta = $monto_bruto * 0.10;
    $liquido = $monto_bruto - $renta;
    
    $response = [
        'monto_bruto' => $monto_bruto,
        'impuestos' => [
            'iva' => round($iva, 2),
            'renta' => round($renta, 2)
        ],
        'monto_liquido' => round($liquido, 2)
    ];
    
    echo json_encode($response);
} else {
    echo json_encode(['error' => 'Falta el monto bruto.']);
}
?>