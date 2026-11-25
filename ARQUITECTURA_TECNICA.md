# ARQUITECTURA TÉCNICA Y EVOLUCIÓN DEL SISTEMA
**Documentación técnica profunda del Extractor de Facturas**

Este documento detalla el stack tecnológico, los patrones de diseño, y la evolución de la infraestructura de Inteligencia Artificial que potencia esta aplicación.

---

## 🛠️ 1. STACK TECNOLÓGICO BASE
- **Lenguaje:** Kotlin (100% Nativo).
- **Interfaz de Usuario (UI):** Jetpack Compose. Se utilizó `NavDisplay` y `ListDetailSceneStrategy` (Navigation 3) para lograr un diseño responsivo y adaptativo (pantallas divididas en tablets y navegación en cascada en teléfonos).
- **Arquitectura de Software:** MVVM (Model-View-ViewModel). El estado de la interfaz se gestiona reactivamente mediante `StateFlow` y eventos unidireccionales.
- **Concurrencia:** Kotlin Coroutines y `Dispatchers.IO` para delegar el trabajo pesado de base de datos, compresión de imágenes y peticiones de red a hilos secundarios, evitando el bloqueo del hilo principal (UI Thread).
- **Persistencia de Datos:** Room Database (SQLite). Base de datos relacional (v6) que soporta migración destructiva y consultas reactivas (`Flow<List>`), estructurada con entidades de `Folder` e `Invoice` (con relaciones One-to-Many mediante `folderId`).

## 🧠 2. LA EVOLUCIÓN DEL MOTOR DE INTELIGENCIA ARTIFICIAL
El núcleo de la aplicación sufrió tres migraciones arquitectónicas mayores para alcanzar el nivel de producción:

### Fase 1: OCR Local (Google ML Kit Text Recognition)
- **Implementación Inicial:** La imagen se procesaba en el teléfono para extraer cadenas de texto sin estructura.
- **El Problema:** ML Kit no comprende jerarquías. En facturas densas o arrugadas, entregaba un bloque de texto desordenado (basura), haciendo imposible una extracción contable exacta mediante expresiones regulares (Regex).

### Fase 2: LLMs y SDK Nativos (Google GenAI / Gemini 1.5 Pro)
- **Pivote a Visión Multimodal:** Se eliminó ML Kit y se migró al SDK oficial de Google para enviar la fotografía cruda directamente al modelo visual de Gemini.
- **El Problema:** Nos enfrentamos a modelos descontinuados repentinamente (Errores 404) y estrictos límites de cuota (Errores 429: 20 peticiones por día / 5 por minuto). La inestabilidad de la API `v1beta` en los servidores gratuitos de Google frenó el desarrollo.

### Fase 3: Arquitectura Definitiva (Groq API + Qwen 3.8 Vision)
- **El Salto Final:** Se eliminaron los SDKs cerrados. Se construyó un cliente HTTP puro usando **OkHttp3**.
- **Procesamiento de Imagen:** La foto se redimensiona a 1024px, se comprime en formato JPEG y se codifica algorítmicamente en una cadena **Base64** pura.
- **Payload y JSON:** Se estructura una petición POST hacia el endpoint de OpenAI-compatibility de Groq. Se envía el Base64 y un "System Prompt" (ingeniería de prompts estricta) para forzar la salida en un esquema JSON (`FacturaExtraida`), controlando la "alucinación" con `temperature = 0.1` y limitando el costo computacional con `max_tokens = 800`.
- **Modelo Utilizado:** `qwen/qwen3.8-27b`, un LLM visual masivo habilitado para alta concurrencia en la API Key actual.

## ⚙️ 3. OPTIMIZACIÓN DE RENDIMIENTO Y RED
- **Prevención de OutOfMemoryError:** Cargar fotos de 12 a 50 Megapíxeles directamente a la RAM crasheaba la máquina virtual (JVM). Se implementó `ImageDecoder.setTargetSampleSize` para submuestrear matemáticamente el mapa de bits *durante* la lectura desde el disco duro, reduciendo el consumo de memoria en un 90%.
- **Tolerancia a Red (Connection Reset):** Los análisis complejos de facturas tomaban mucho tiempo, provocando que OkHttp cerrara el socket (Protocol Error / Stream Reset en HTTP/2). Se resolvió forzando la degradación del socket a `Protocol.HTTP_1_1` y elevando los Timeouts a 120 segundos.

## 🔗 4. HERRAMIENTAS HÍBRIDAS E INTEGRACIÓN NATIVA
- **Escáner QR Híbrido:** Dado que los LLMs no pueden decodificar códigos bidimensionales desde los píxeles, se re-integró el módulo `barcode-scanning` de ML Kit. La app corre un proceso en paralelo: mientras la IA lee el texto, ML Kit busca el QR, extrae el enlace de verificación del SAT y sobrescribe cualquier error de la IA.
- **Exportación con FileProvider:** Para enviar el correo electrónico con la fotografía adjunta, se usó un `Intent.ACTION_SEND`. Debido al sistema de permisos de Android (Scoped Storage), se implementó un `FileProvider` con la bandera `FLAG_GRANT_READ_URI_PERMISSION`, delegando temporalmente el acceso de lectura de nuestro *Uri* a la aplicación de correo (Gmail/Outlook).