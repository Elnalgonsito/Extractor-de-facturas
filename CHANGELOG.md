# Historial de Cambios y Mejoras (Changelog)

## Etapa 45: Perfeccionamiento de Identidad (Color Matching Exacto)
- **Extracción de Colores Nativos:** Se analizó el logotipo original del proyecto para extraer los códigos hexadecimales exactos.
- **Modo Oscuro Inmersivo (Navy & Neon):** El fondo de la aplicación ahora coincide exactamente con el "Azul Naval Profundo" (`#0A111F`) del logo. Los acentos, letras y botones principales ahora brillan con el "Verde Neón/Menta" (`#1DF2A4`) y el "Cian" (`#02B8DF`), creando una experiencia visual envolvente, tecnológica y 100% fiel a la marca original.

## Etapa 44: Forzado de Identidad Visual (Color Theming)
- **Aplicación de Paleta:** Se detectó que el tema Material 3 heredaba colores base del sistema por defecto. Se modificó el código de todas las pantallas para inyectar forzosamente los colores "Celeste" y "Verde" en los componentes clave (Barras superiores, Botones Flotantes, y Tarjetas), haciendo que la aplicación finalmente refleje la identidad de la marca.

## Etapa 43: Diseño UI/UX (Verde & Celeste) y Animaciones
- **Identidad de Marca Perfeccionada:** Se reescribió la paleta de colores de Material Design 3 utilizando los tonos exactos del logotipo del cliente (Celeste vibrante y Verde corporativo), unificando el frontend con la identidad visual.
- **Animaciones y Transiciones (UX):** Se agregaron animaciones de entrada (`AnimatedVisibility`, `slideInVertically`, `fadeIn`) a todas las pantallas principales. Además, los formularios ahora soportan `animateContentSize()`, permitiendo que el llenado automático de la Inteligencia Artificial se vea suave, fluido y orgánico al aparecer en pantalla.

## Etapa 42: Branding y Tematización
- **Logo Oficial:** Se integró el logo oficial de la aplicación (`app_logo.webp`) reemplazando los íconos genéricos de Android en el `AndroidManifest.xml`.
- **Paleta de Colores Dinámica:** Se extrajeron los colores corporativos predominantes del nuevo logo mediante un script. La interfaz gráfica completa (Material 3) fue rediseñada para adoptar esta nueva paleta (Azul Oscuro Corporativo) como colores primarios, secundarios y terciarios, unificando la identidad visual tanto en el tema claro como oscuro.

## Etapa 41: Auditoría Fiscal Inteligente (Validación SAT)
- **Reutilización Estratégica de Campos:** Se replanteó el propósito del campo "Link para facturar" dentro del contexto de Facturas. Dado que una factura ya no necesita facturarse, este campo ahora se transforma dinámicamente en "Enlace de Verificación (SAT)". 
- **Propósito y Beneficios:** Aprovechando que el escáner híbrido extrae el código QR nativo del CFDI, esta URL se utiliza ahora para comprobación fiscal. Se agregó un botón de acceso directo que abre la página oficial del SAT en el navegador, permitiendo al usuario (o al contador) auditar con un solo toque si la factura está "Vigente" o "Cancelada", previniendo fraudes y agilizando la revisión contable sin salir del flujo de trabajo.

## Etapa 40: Legibilidad de Catálogos CFDI (Forma y Método de Pago)
- **Corrección de Extracción de Pagos:** Se modificó la instrucción raíz de la Inteligencia Artificial. Anteriormente, la IA extraía las claves de catálogo del SAT para los métodos de pago (ej. `04` o `PUE`). Ahora extrae forzosamente las descripciones legibles para el humano (ej. "Tarjeta de crédito", "Pago en una sola exhibición") para evitar confusiones en la interfaz de usuario.

## Etapa 39: Refinamiento de Interfaz para Facturas
- **Corrección de Valores Cero (IEPS):** Se corrigió un error lógico en la pantalla donde los impuestos con valor `0.0` (como el IEPS en gasolina estándar) se ocultaban y dejaban el campo en blanco. Ahora se muestran explícitamente como `0.0`.
- **Formato de UUID:** Se ajustó el campo de texto del Folio Fiscal para permitir múltiples líneas, evitando cortes extraños en la cadena de 36 caracteres.

## Etapa 38: Estabilidad de Conexión (HTTP/2 Protocol Error)
- **Degradación Forzada de Protocolo:** Se detectó que el servidor de Groq cortaba la conexión arrojando `stream was reset: PROTOCOL_ERROR` al procesar cargas útiles grandes de Facturas bajo el protocolo HTTP/2. Se reconfiguró el cliente HTTP para forzar el uso de `HTTP/1.1`, garantizando que la transmisión de datos se mantenga estable y abierta hasta que la IA termine de analizar la factura.

## Etapa 35: Interfaz y Base de Datos Exclusiva para Facturas
- **Formulario CFDI Dinámico:** Se construyó una interfaz de usuario especializada que se despliega únicamente cuando el usuario escanea una "Factura". Este formulario incluye campos fiscales como UUID, RFCs, Uso CFDI y Método de Pago.
- **Esquema de Base de Datos:** Se expandió la entidad `Invoice` en Room (v6) para almacenar permanentemente la metadata fiscal.
- **Resumen Contable:** Se implementó un generador de reportes específico para Facturas que formatea la información para los correos electrónicos.

## Etapa 33: Soporte Multi-Documento (Tickets y Facturas)
- **Pantalla de Selección de Documentos:** Se diseñó y construyó una nueva pantalla de inicio que le pregunta al usuario qué tipo de documento desea procesar (Ticket o Factura), preparando el terreno para flujos especializados.
- **Aislamiento de Bases de Datos:** Se modificó la tabla de Carpetas (`Folders`) en Room para incluir el tipo de documento. Ahora, las carpetas de "Tickets" y "Facturas" viven en mundos separados y no se mezclan en la interfaz.

## Etapa 32: Visor Interactivo de Imágenes (Zoom y Paneo)
- **Exploración a Detalle:** Se implementó una función para ampliar la fotografía original del ticket dentro de la pantalla de detalles.
- **Gestos Táctiles:** Al tocar la imagen en miniatura, se despliega un visor a pantalla completa que soporta gestos nativos (Pinch-to-zoom para acercar/alejar y Paneo para desplazarse por la imagen), permitiéndole al usuario auditar los detalles microscópicos de la factura antes de guardarla o enviarla.

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

## Etapa 25: Resolución de Acceso a Modelos Groq
- **Cambio de Motor Multimodal (Qwen):** Se detectó que las llaves API de Groq del usuario no tienen permisos habilitados para usar la familia `llama-vision`. El motor se migró a `qwen/qwen3.8-27b`, el cual es el único modelo de visión de última generación disponible y habilitado bajo la cuota de la cuenta actual para evitar errores 404 de permisos.

## Etapa 26: Organización por Carpetas y Gestión de Archivos
- **Estructura de Carpetas:** Se rediseñó el flujo de la aplicación. Ahora la pantalla principal muestra una cuadrícula de carpetas personalizables.
- **Filtro de Tickets:** Los tickets ahora se guardan y se filtran específicamente dentro de la carpeta seleccionada.
- **Borrado de Datos:** Se implementó la capacidad de eliminar tickets individualmente (ícono de papelera en los detalles) y eliminar carpetas completas.
- **Migración de Base de Datos:** Se actualizó el esquema de Room para soportar relaciones (Folder -> Invoices).

## Etapa 27: Escáner Híbrido de Códigos QR (ML Kit)
- **Detección de URLs Exactas:** La Inteligencia Artificial es excelente leyendo texto, pero no puede decodificar códigos bidimensionales. Se re-integró Google ML Kit específicamente con su módulo `barcode-scanning` para detectar códigos QR en las fotografías de los tickets.
- **Fusión de Datos:** Si el ticket (como los de Farmacias Guadalajara) contiene un QR para facturar, el escáner lo lee de forma nativa e inyecta la URL 100% precisa en el campo "Link para facturar", sobrescribiendo cualquier enlace alucinado por la IA.

## Etapa 28: Optimización de Cuotas (Max Tokens)
- **Control de Longitud de Salida:** Se detectó un límite estricto de Groq de 1000 tokens de salida por minuto para el nivel gratuito. Se agregó el parámetro explícito `max_tokens = 800` y `temperature = 0.1` al cuerpo de la petición HTTP, asegurando que la respuesta de la IA siempre se mantenga por debajo del límite de bloqueo (Error 429).

## Etapa 29: Corrección de Exportación por Correo
- **Cuerpo de Correo Dinámico:** Se corrigió un error donde el correo se enviaba vacío ("Sin detalles") al abrir facturas previamente guardadas. El cuerpo del correo ahora se construye en tiempo real leyendo directamente los campos de texto de la pantalla, garantizando que incluya el Subtotal, IVA, IEPS y cualquier edición manual que haya hecho el usuario.

## Etapa 30: Fusión de Resumen IA en Exportación
- **Mejora de Correo Electrónico:** Se actualizó la plantilla del correo de exportación para incluir tanto los datos extraídos/editables en la interfaz gráfica (Subtotal, IVA, etc.) como el bloque original del "Resumen del Ticket" generado por la IA en la parte inferior, brindando contexto completo al contador o destinatario final.

## Etapa 31: Adjuntar Fotografías en Exportación por Correo
- **Compartir Archivos Seguros:** Se modificó la estructura del botón de exportación para utilizar `Intent.ACTION_SEND` (en lugar de SENDTO) con el fin de soportar archivos adjuntos.
- **Delegación de Permisos:** Ahora, si la factura contiene una fotografía original (URI), la aplicación concede temporalmente permisos de lectura a la aplicación de correo (Gmail, Outlook, etc.) y adjunta la imagen automáticamente junto con el cuerpo de texto, completando el ecosistema de exportación.

## Etapa 34: Arquitectura Avanzada para Facturas (CFDI)
- **Modelos de Datos CFDI:** Se crearon estructuras de datos serializables especializadas (`FacturaExtraida`, `Emisor`, `ConceptoFactura`) para capturar la complejidad de las facturas mexicanas, incluyendo UUID, sellos y desglose de IEPS/IVA a nivel de artículo.
- **Doble Flujo Inteligente:** El motor de IA ahora distingue si se está leyendo un Ticket o una Factura y utiliza un prompt especializado con validaciones matemáticas estrictas, asegurando la coherencia entre el subtotal, impuestos y total.

## Etapa 36: Tolerancia a Latencia (Connection Reset)
- **Optimización de Red:** Dado que el procesamiento de Facturas CFDI requiere extraer decenas de campos anidados, el tiempo de respuesta del servidor (Groq) se incrementó considerablemente. Se ampliaron los tiempos de espera del cliente HTTP (Timeouts a 120s) y se habilitó un auto-reintento nativo para evitar el error "Connection Reset" causado por cortes de red durante análisis prolongados.

## Etapa 37: Corrección de Mapeo de Datos CFDI
- **Vinculación de UI para Facturas:** Se corrigió un error arquitectónico donde la interfaz gráfica no mostraba los datos del proveedor al escanear una Factura. Ahora la pantalla mapea correctamente los datos anidados (`emisor.razonSocial`) hacia los campos editables del formulario.
