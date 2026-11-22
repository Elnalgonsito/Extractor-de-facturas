# Project Plan

Una aplicación nativa de Android para escanear y gestionar facturas. La pantalla principal debe tener una lista (LazyColumn) que muestre un historial de facturas (cada elemento debe mostrar el nombre del proveedor, la fecha y el monto total) y un Floating Action Button (FAB) con un ícono de cámara. Al presionar el FAB, debe navegar a una pantalla de confirmación que contenga un formulario con campos de texto (TextFields) para 'Fecha', 'Proveedor' y 'Monto Total', además de un botón grande para 'Guardar'. Utiliza Jetpack Compose y Material Design 3.

## Project Brief

# Project Brief: Invoice Extractor App (Extractor de Facturas)

## Features
1. **Invoice History Dashboard:** A primary main screen featuring a scrollable list (`LazyColumn`) of previously saved invoices. Each list item displays key details: Provider Name, Date, and Total Amount.
2. **Capture Action Trigger:** A Floating Action Button (FAB) with a camera icon on the main screen that seamlessly navigates the user to the data entry flow.
3. **Invoice Confirmation Form:** A dedicated confirmation screen containing editable text fields (`TextFields`) for 'Date', 'Provider', and 'Total Amount' to verify and input invoice details.
4. **Save & Store Functionality:** A prominent 'Save' button within the form to persist the invoice data and update the history list.

## High-Level Tech Stack
*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose with Material Design 3 components.
*   **Navigation & Adaptive Strategy:** Jetpack Navigation 3 (state-driven) and the Compose Material Adaptive library for all layouts.
*   **Architecture & Concurrency:** MVVM Architecture, Android ViewModel, and Kotlin Coroutines for asynchronous operations.
*   **Persistence:** Room Database (required to store, manage, and retrieve the saved invoice history).

## Implementation Steps

### Task_37_UpdateModelToQwen: Revert the models-list debug hack in GeminiInvoiceParser.kt. Set the Groq payload model to 'qwen/qwen3.8-27b'. Update CHANGELOG.md and push changes to Git.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Models-list hack is removed from GeminiInvoiceParser.kt
  - Model is set to qwen/qwen3.8-27b
  - CHANGELOG.md updated and code pushed to Git
- **StartTime:** 2026-11-22 15:24:43 MST

### Task_38_RunAndVerify: Run and verify application stability. Instruct critic_agent to verify that the app successfully parses the image using the Qwen model on Groq and does not return a 404 error.
- **Status:** PENDING
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash

