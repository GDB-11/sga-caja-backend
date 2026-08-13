package com.cibertec.sga.expense.domain.repository;

import com.cibertec.sga.expense.domain.model.ExpenseBulkUploadRef;

/**
 * Puerto de persistencia para el lote de carga masiva de egresos (RF-28), implementado en
 * {@code infrastructure}. Sin lectura/listado propio: el único consumidor es
 * {@code ExpenseService}, que crea el lote (con su estado final ya resuelto, {@code Processed}
 * o {@code Failed}) antes de insertar los egresos que le pertenecen.
 */
public interface IExpenseBulkUploadRepository {

    ExpenseBulkUploadRef create(String fileName, String statusName);
}
