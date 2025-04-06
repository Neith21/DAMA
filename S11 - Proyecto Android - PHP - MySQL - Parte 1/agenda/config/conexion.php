<?php
function conectarDB() {
    $host = "localhost";
    $dbname = "miagenda";
    $user = "root";
    $password = "123123";
    
    try {
        $pdo = new PDO("mysql:host=$host;dbname=$dbname", $user, $password);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $pdo;
    } catch (PDOException $e) {
        die(json_encode(['error' => 'Error de conexión: ' . $e->getMessage()]));
    }
}
