package com.sistema_de_vacunacion.Delta.model;




public class enums {
    
public enum TipoDocumento { RC, TI, CC, CE, PA }

public enum Genero { MASCULINO, FEMENINO, OTRO }

public enum EstadoUsuario { ACTIVO, INACTIVO }

public enum TipoAccionAuditoria { CREAR, EDITAR, ELIMINAR, CONSULTAR }

public enum ViaAdministracion { ORAL, INTRAMUSCULAR, SUBCUTANEA, INTRADERMICA }

public enum EstadoVacuna { ACTIVA, INACTIVA }

public enum NumeroDosis { PRIMERA, SEGUNDA, TERCERA, REFUERZO, UNICA }

public enum EstadoRecordatorio { PENDIENTE, ENVIADO, FALLIDO }

public enum CriterioCalculo { POR_EDAD, POR_INTERVALO }

}
