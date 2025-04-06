<?php
function mostrarError($mensaje) {
    echo json_encode(['error' => $mensaje]);
    exit;
}
?>