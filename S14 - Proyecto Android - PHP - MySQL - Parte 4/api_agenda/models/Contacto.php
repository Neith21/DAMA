<?php
require_once '../config/Conexion.php';

class Contacto
{
    private $conexion;
    
    public function __construct() {
        $this->conexion = new Conexion();
    }

    /**
     * Valida y sanitiza un string
     */
    private function sanitizarString($texto) {
        if (!is_string($texto)) {
            return '';
        }
        $texto = trim($texto);
        $texto = htmlspecialchars($texto, ENT_QUOTES, 'UTF-8');
        return $texto;
    }
    
    /**
     * Valida un número telefónico (solo números y algunos caracteres especiales)
     */
    private function validarTelefono($telefono) {
        if (empty($telefono)) {
            throw new Exception("El teléfono no puede estar vacío");
        }
        
        // Permitir números, espacios, guiones, paréntesis y el signo +
        if (!preg_match('/^[0-9\s\-\(\)\+]+$/', $telefono)) {
            throw new Exception("El formato del teléfono no es válido");
        }
        
        return $telefono;
    }
    
    /**
     * Valida el nombre
     */
    private function validarNombre($nombre) {
        $nombre = $this->sanitizarString($nombre);
        
        if (empty($nombre)) {
            throw new Exception("El nombre no puede estar vacío");
        }
        
        if (strlen($nombre) < 2 || strlen($nombre) > 100) {
            throw new Exception("El nombre debe tener entre 2 y 100 caracteres");
        }
        
        return $nombre;
    }
    
    /**
     * Valida el ID
     */
    private function validarId($id) {
        $id = filter_var($id, FILTER_VALIDATE_INT);
        
        if ($id === false || $id <= 0) {
            throw new Exception("ID de contacto inválido");
        }
        
        return $id;
    }

    // SELECT: Obtener todos los contactos
    public function listarContactos()
    {
        try {
            $stmt = $this->conexion->idConexion->prepare("SELECT id_contacto, nombre, telefono FROM contactos");
            $stmt->execute();
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contactos: " . $e->getMessage());
        }
    }

    // SELECT: Obtener un contacto por ID
    public function listarContactoPorId($id_contacto)
    {
        try {
            $id_contacto = $this->validarId($id_contacto);
            
            $stmt = $this->conexion->idConexion->prepare("SELECT id_contacto, nombre, telefono FROM contactos WHERE id_contacto = :id");
            $stmt->bindParam(':id', $id_contacto, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contacto: " . $e->getMessage());
        }
    }

    // SELECT: Obtener contactos con filtro
    public function listarContactosConFiltro($filtro)
    {
        try {
            $filtro = $this->sanitizarString($filtro);
            $filtroParam = "%$filtro%";
            
            $consulta = "SELECT id_contacto, nombre, telefono FROM contactos WHERE (nombre LIKE :filtro OR telefono LIKE :filtro) ORDER BY nombre ASC";
            $stmt = $this->conexion->idConexion->prepare($consulta);
            $stmt->bindParam(':filtro', $filtroParam, PDO::PARAM_STR);
            $stmt->execute();
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } catch (PDOException $e) {
            throw new Exception("Error al listar contactos con filtro: " . $e->getMessage());
        }
    }

    // INSERT: Agregar un nuevo contacto
    public function insertarContacto($nombre, $telefono)
    {
        try {
            $nombre = $this->validarNombre($nombre);
            $telefono = $this->validarTelefono($telefono);
            
            $stmt = $this->conexion->idConexion->prepare("INSERT INTO contactos (nombre, telefono) VALUES (:nombre, :telefono)");
            $stmt->bindParam(':nombre', $nombre, PDO::PARAM_STR);
            $stmt->bindParam(':telefono', $telefono, PDO::PARAM_STR);
            $stmt->execute();
            return $this->conexion->idConexion->lastInsertId();
        } catch (PDOException $e) {
            throw new Exception("Error al insertar contacto: " . $e->getMessage());
        }
    }

    // UPDATE: Actualizar un contacto existente
    public function actualizarContacto($id_contacto, $nombre, $telefono)
    {
        try {
            $id_contacto = $this->validarId($id_contacto);
            $nombre = $this->validarNombre($nombre);
            $telefono = $this->validarTelefono($telefono);
            
            $stmt = $this->conexion->idConexion->prepare("UPDATE contactos SET nombre = :nombre, telefono = :telefono WHERE id_contacto = :id");
            $stmt->bindParam(':nombre', $nombre, PDO::PARAM_STR);
            $stmt->bindParam(':telefono', $telefono, PDO::PARAM_STR);
            $stmt->bindParam(':id', $id_contacto, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount();
        } catch (PDOException $e) {
            throw new Exception("Error al actualizar contacto: " . $e->getMessage());
        }
    }

    // DELETE: Eliminar un contacto
    public function eliminarContacto($id_contacto)
    {
        try {
            $id_contacto = $this->validarId($id_contacto);
            
            $stmt = $this->conexion->idConexion->prepare("DELETE FROM contactos WHERE id_contacto = :id");
            $stmt->bindParam(':id', $id_contacto, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->rowCount();
        } catch (PDOException $e) {
            throw new Exception("Error al eliminar contacto: " . $e->getMessage());
        }
    }
}