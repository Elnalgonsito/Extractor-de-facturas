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
**Total Duration:** 35h 15m 27s

### Task_1_RoomPersistence: Set up Room database for Invoices including Entity (Date, Provider, Total Amount), DAO, and Repository.
- **Status:** COMPLETED
- **Updates:** Room dependencies verified. Invoice Entity, DAO, AppDatabase, and Repository pattern implemented successfully. Project builds.
- **Acceptance Criteria:**
  - Room dependencies added
  - Invoice Entity and DAO created
  - Repository pattern implemented
- **Duration:** 1h 2m 24s

### Task_2_ViewModel: Create ViewModel using Coroutines to manage UI state, fetch invoice history from Room, and handle saving new invoices.
- **Status:** COMPLETED
- **Updates:** ViewModel implemented successfully with StateFlow for invoice list and save functionality persisting data via repository.
- **Acceptance Criteria:**
  - ViewModel implements StateFlow for invoice list
  - Save functionality persists data via repository
- **Duration:** 1h 9m 36s

### Task_3_ComposeUINavigation: Implement Jetpack Navigation 3 and Adaptive layouts. Create Main Screen (LazyColumn of invoices + FAB) and Confirmation Screen (TextFields for Date, Provider, Total and Save button) using Material Design 3.
- **Status:** COMPLETED
- **Updates:** Implemented Jetpack Navigation 3 with NavDisplay and rememberNavBackStack. Created Main Screen with LazyColumn and FAB. Created Confirmation Screen with form and Save button. Utilized Compose Material 3 and adaptive layouts with ListDetailSceneStrategy.
- **Acceptance Criteria:**
  - Navigation 3 implemented
  - Main Screen shows LazyColumn and FAB
  - Confirmation Screen shows form and Save button
  - UI uses Compose Material 3
- **Duration:** 1h 2m 36s

### Task_5_PhotoPickerAndOCR: Add Google ML Kit Text Recognition dependency. Update Main Screen FAB to launch Android Photo Picker. Process selected image to extract text and pass the extracted text to Confirmation Screen.
- **Status:** COMPLETED
- **Updates:** Photo Picker implemented on FAB click. ML Kit Text Recognition processes the selected image and extracts text. Navigation updated to pass the extracted text to the Confirmation Screen. Confirmation Screen now displays the extracted text.
- **Acceptance Criteria:**
  - ML Kit dependency added
  - FAB opens Photo Picker
  - Extracted text is passed to Confirmation Screen
- **Duration:** 1h 2m 38s

### Task_6_RunAndVerify: Run and verify application stability. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Critic agent successfully built and verified the app. No crashes detected. Navigation and OCR pipeline work correctly on phone UI. The adaptive large-screen layout was skipped as only a phone emulator was available. No critical UI issues.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 1h 1m 59s

### Task_7_RegexParsingAndAutoFill: Create 'extraerDatosDelTicket(textoCrudo: String)' using Regex to parse dates (dd/mm/yyyy, yyyy-mm-dd) and totals ('TOTAL' or '$' + decimals). Update Confirmation Screen to auto-fill the state variables for Date and Total Amount upon receiving OCR text.
- **Status:** COMPLETED
- **Updates:** Implemented `extraerDatosDelTicket` using Regex in a separate utility file. Integrated it into the `InvoiceConfirmationScreen` to parse the `initialExtractedText` and pre-fill the `date` and `totalAmount` state variables. Added unit tests for parsing accuracy.
- **Acceptance Criteria:**
  - Regex successfully extracts Date and Total
  - UI auto-fills from parsed OCR text
- **Duration:** 1h 1m 32s

### Task_8_FixSaveAndVerify: Fix Invoice Details save button to store actual current text field values (Provider, Date, Total) rather than initial empty states. Run and verify application stability and proper database saving.
- **Status:** COMPLETED
- **Updates:** Coder fixed the save functionality to use current typed states and correctly connected it to the Room database ViewModel instead of mock data. Critic agent verified that OCR parses text, pre-fills date and total, allows editing provider, and successfully saves to the database which updates the main list. A minor issue with duplicate saves on double tap was noted as an area for improvement.
- **Acceptance Criteria:**
  - Save functionality captures typed state values correctly
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 26h 9m

### Task_9_GeminiIntegration: Add Google Generative AI SDK dependency and a JSON parsing library (e.g., kotlinx.serialization). Configure API key via local.properties and BuildConfig. Create an AI service class with Coroutines that prompts Gemini to parse raw OCR text into a structured JSON format containing Provider, Date, and Total Amount.
- **Status:** COMPLETED
- **Updates:** Generative AI SDK added, API key configured in local.properties and BuildConfig. GeminiInvoiceParser created. User provided the API key successfully.
- **Acceptance Criteria:**
  - Generative AI SDK added
  - API_KEY integration via local.properties and BuildConfig is complete
  - AI service successfully returns structured JSON data from raw text
- **Duration:** 1h 41m 20s

### Task_10_UpdateUIAndVerify: Update ViewModel and Invoice Confirmation Screen to use the AI service for pre-filling the Provider, Date, and Total Amount fields asynchronously. Run and verify application stability. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements (AI populates fields accurately), and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** ViewModel/UI updated to use Gemini for asynchronous text parsing. Critic agent verified that selecting an image successfully queries Gemini and accurately populates the Provider, Date, and Total fields. The save functionality was also verified to work smoothly post AI-extraction.
- **Acceptance Criteria:**
  - UI auto-fills Provider, Date, and Total Amount from AI response
  - make sure all existing tests pass
  - build pass
  - app does not crash
- **Duration:** 1h 4m 22s

