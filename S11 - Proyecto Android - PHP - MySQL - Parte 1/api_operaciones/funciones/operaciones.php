<?php
function realizarOperacion($accion, $valor1, $valor2) {
    global $operaciones_permitidas;
    
    if (!in_array($accion, $operaciones_permitidas)) {
        return ['error' => 'Acción no soportada.'];
    }
    
    switch ($accion) {
        case 'suma':
            return $valor1 + $valor2;
        case 'resta':
            return $valor1 - $valor2;
        case 'multiplicacion':
            return $valor1 * $valor2;
        case 'division':
            if ($valor2 == 0) {
                return ['error' => 'No se puede dividir por cero.'];
            }
            return $valor1 / $valor2;
    }
}
?>