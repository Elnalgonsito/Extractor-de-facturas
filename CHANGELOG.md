# Historial de Cambios y Mejoras (Changelog)

## Etapa 1: Arquitectura Base e Interfaz Gráfica
- **Base de Datos (Room):** Configuración inicial con Entidad, DAO y Repositorio para guardar facturas.
- **Gestión de Estado (ViewModel):** Implementación de MVVM usando Kotlin Coroutines y StateFlow.
- **UI y Navegación (Jetpack Compose):** Creación de la Lista de Facturas y Formulario de Confirmación usando Material Design 3, y Jetpack Navigation 3 con soporte para pantallas adaptativas.

## Etapa 2: OCR y Captura de Tickets
- **Photo Picker:** Conexión del botón FAB para abrir la galería nativa de Android y seleccionar imágenes de tickets.
- **Google ML Kit:** Integración de la librería de reconocimiento de texto (Text Recognition) para extraer en crudo toda la información de las imágenes.

## Etapa 3: Inteligencia Artificial y Auto-llenado
- **Procesamiento de Texto (Gemini AI):** Implementación del SDK de Google Generative AI. Creación de un servicio asíncrono para estructurar el texto "crudo" de ML Kit a un formato JSON.
- **Fallback de Regex:** Expresiones regulares creadas para una extracción instantánea de Monto y Fecha en lo que la IA procesa.
- **Auto-llenado UI:** Lógica para actualizar los campos del formulario (Proveedor, Fecha, Total Amount) de manera automática tras la lectura.

## Etapa 4: Expansión de Base de Datos e Integración con Sistema
- **Actualización de Esquema (Room):** Se agregaron los campos `imagenUri`, `textoCrudo`, `linkFactura` y `correoDestino` a la base de datos con migración destructiva (v2).
- **Visualización de Imágenes (Coil):** Uso de `AsyncImage` para visualizar el ticket escaneado original dentro de la pantalla de detalles.
- **Envío de Correo Electrónico:** Implementación de botón con `Intent.ACTION_SENDTO` nativo para exportar y compartir los datos extraídos por correo.
- **Prompt Avanzado:** Mejora radical en las instrucciones para la IA para extraer enlaces/URLs de portales de facturación, con validaciones robustas.

## Etapa 5: Extracción Contable Avanzada
- **Prompt Contable (Gemini):** Refinamiento drástico del prompt para instruir a la IA a actuar como analista de datos contables. Filtrado automático de ruido (encuestas, puntos).
- **Modelado Complejo de Datos:** Expansión de `InvoiceData` y creación de `InvoiceItem` para soportar estructura JSON compleja (sucursal, método de pago, régimen fiscal, desglose de artículos individuales, subtotal e impuestos).

## Etapa 6: Resumen Estructurado en UI
- **Transformación de Datos:** Creación de la función `generarResumenLimpio` para convertir el objeto de datos complejo extraído por la IA en un String estructurado y legible con iteración de artículos.
- **Limpieza de Interfaz:** Reemplazo del bloque de "Texto Crudo" (OCR) en la pantalla de detalles por un resumen limpio generado dinámicamente.

## Etapa 7: Exportación Avanzada
- **Correo Electrónico Dinámico:** Actualización del cuerpo del correo para inyectar directamente el resumen estructurado por la IA, utilizando un espaciado doble (`lineasEspaciado = 2`) para una presentación más formal.

## Etapa 8: Refinamiento Estricto de Prompt
- **Prompt API Estricta:** Modificación del prompt de Gemini para obligarlo a comportarse como una API estricta, removiendo etiquetas markdown y filtrando basura específica de tiendas.
- **Reestructuración JSON:** División de fecha y hora, y conversión del campo de impuestos a una lista anidada de objetos detallando tipo y monto.

## Etapa 9: Formateador Dinámico desde JSON
- **Parsing Directo:** Creación de la función `formatearTicketLimpio` para aceptar directamente un String (JSON) extraído, validarlo/parsearlo, y generar el resumen dinámico. Manejo de excepciones integrado con mensaje de error controlado.

## Etapa 10: Limpieza Definitiva de UI y Correo
- **Eliminación de Texto OCR:** Remoción permanente del texto "crudo" del flujo visible y de los exportables (email). Todo se apoya ahora exclusivamente en el formateador limpio y estructurado `formatearTicketLimpio` alimentado por el JSON generado por IA.

## Etapa 11: Manejo de Errores y Limpieza de Markdown
- **Resiliencia en Parsing:** Implementación de `try-catch` en la corrutina de análisis para capturar errores de formato o red, actualizando proactivamente los campos de la UI con mensajes de error descriptivos.
- **Sanitización de JSON:** Eliminación forzada de etiquetas Markdown (` ```json `) de la respuesta de Gemini antes del parseo para prevenir crashes silenciosos.