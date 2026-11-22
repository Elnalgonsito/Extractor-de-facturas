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

## Etapa 12: Tolerancia a Nulos en Parsing JSON
- **Robustez de Serialización:** Configuración avanzada de `kotlinx.serialization` (`explicitNulls = false`, `coerceInputValues = true`) para garantizar un parseo 100% tolerante a fallos. Si la IA omite arrays o devuelve campos incompletos, el sistema asignará nulos por defecto en lugar de lanzar excepciones, preservando los datos que sí fueron detectados.

## Etapa 13: Corrección de Alias de Modelo IA
- **Resolución de Error 404:** Se actualizó el identificador del modelo generativo de `"gemini-1.5-flash"` a `"gemini-1.5-flash-latest"` para solucionar el error 404 (NOT_FOUND) provocado por la API v1beta del SDK de Google.

## Etapa 14: Corrección de Alias de Modelo IA (Intento 2)
- **Resolución de Error 404:** Se actualizó el identificador del modelo generativo a `"gemini-1.5-pro"` (y se preparó el fallback a `"gemini-pro"`) dado que la región o la versión de la API v1beta no soportaba los modelos flash.

## Etapa 15: Migración a Interactions API (Google GenAI)
- **Actualización de SDK:** Se eliminó la librería `generativeai` (deprecada) y se integró el nuevo SDK oficial `com.google.genai:google-genai`.
- **Nueva Infraestructura:** Se refactorizó la consulta de la IA utilizando `CreateModelInteraction` y `Client` para acceder al modelo `gemini-3.8-flash` y resolver las saturaciones e incompatibilidades del SDK anterior.

## Etapa 16: Desglose y Cálculo Automático de Impuestos
- **Cálculo de IVA e IEPS:** Se modificó el prompt de la Inteligencia Artificial para forzar matemáticamente el cálculo de IVA (16%) e IEPS (8%) en caso de que el ticket original no los incluya explícitamente.
- **Campos en Base de Datos y UI:** Se actualizó la entidad `Invoice` de Room (versión 3) añadiendo `subtotal`, `iva`, e `ieps`. Se agregaron los campos editables correspondientes en la pantalla de detalles.

## Etapa 17: Migración a Gemini Vision (Fin de ML Kit)
- **Eliminación de OCR Local:** Se eliminó la dependencia de Google ML Kit Text Recognition por su incapacidad para procesar documentos densos.
- **Gemini Vision (Multimodal):** Se migró la arquitectura para enviar la imagen original (Bitmap) directamente al modelo `gemini-1.5-pro` a través del SDK `generativeai`, permitiendo que la IA utilice su capacidad visual para extraer el JSON estructural con una precisión superior.

## Etapa 18: Estabilización de Arquitectura y Manejo de Límites de Cuota (429)
- **Implementación Definitiva:** Se estabilizó la conexión multimodal utilizando el SDK oficial `com.google.genai`, convirtiendo los Bitmaps a flujos de bytes (JPEG) y empaquetándolos bajo la estructura moderna `Part`.
- **Manejo de Errores Quota:** El sistema es capaz de interceptar de forma limpia los errores `429 RESOURCE_EXHAUSTED`, brindándole visibilidad al usuario de que los límites del plan gratuito de Google han sido alcanzados y previniendo que la aplicación se congele.

## Etapa 19: Corrección de Fugas de Memoria (Infinite Loading)
- **Prevención de OutOfMemoryError:** Se implementó una decodificación segura de imágenes (`setTargetSampleSize`) para comprimir fotografías nativamente antes de subirlas a la RAM, resolviendo el "cuelgue" silencioso al cargar fotos de alta resolución.
- **Resiliencia Total:** Se actualizó el manejador de corrutinas para atrapar `Throwable` (en lugar de solo `Exception`), garantizando que la interfaz siempre reciba retroalimentación incluso frente a errores fatales de la Máquina Virtual.

## Etapa 20: Estabilización Definitiva del Modelo
- **Downgrade Estratégico:** Se cambió el modelo de generación de `gemini-3.8-flash` a `gemini-1.5-flash`. Esto soluciona los constantes errores 503 (Servidores Saturados) causados por la inestabilidad de las versiones experimentales, garantizando una extracción de datos multimodal fluida, rápida y sin interrupciones.

## Etapa 21: Compatibilidad de Modelos con el Nuevo SDK GenAI
- **Salto Generacional:** Se actualizó el modelo a `gemini-2.5-flash` debido a que la versión 1.5 fue oficialmente retirada y descontinuada de la nueva API `com.google.genai` (Error 404). Esta versión asegura compatibilidad total con la nueva arquitectura.

## Etapa 22: Resolución Definitiva de Cuotas y Modelos (Auto-Reintento)
- **Fijación de Modelo Forzoso:** Se fijó el modelo a `gemini-3.8-flash` de manera definitiva, ya que Google bloqueó el acceso a modelos antiguos para cuentas nuevas en el SDK de Interactions.
- **Algoritmo de Auto-Reintento (Backoff):** Para lidiar con el estricto candado gratuito de "5 peticiones por minuto" (Error 429) y saturaciones (Error 503), se implementó un ciclo de auto-reintento inteligente. Si la IA rechaza la conexión, la aplicación pausa silenciosamente durante 32 segundos y lo vuelve a intentar automáticamente hasta 3 veces antes de arrojar un error, garantizando que la extracción se logre sin frustrar al usuario.

## Etapa 23: Pivote a Groq (LLaVA Vision)
- **Reemplazo de Motor IA:** Debido a las extremas restricciones del plan gratuito de Google Gemini, se eliminaron todas las dependencias de Google GenAI y se construyó un cliente HTTP nativo con `OkHttp`.
- **Integración de Groq:** La aplicación ahora se comunica directamente con la API de Groq, utilizando su modelo multimodal de código abierto `llama-3.2-11b-vision-preview`, procesando las imágenes codificadas en Base64 de forma ultra-rápida y evitando por completo los errores 429.

## Etapa 24: Actualización de Modelo Groq
- **Cambio de Alias:** Se actualizó el modelo de generación de `llama-3.2-11b-vision-preview` a `llama-3.2-11b-vision-instruct` debido a la desactivación del entorno de pruebas (preview) por parte de Groq.