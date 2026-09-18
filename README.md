# QA Automation Portfolio — Tienda Demo

Monorepo de automatización de pruebas para un **SUT (System Under Test) propio**: una
tienda e-commerce demo (sin pagos reales, sin registro de usuarios) sobre la que se
ejecuta una suite **black-box** de API, UI/E2E y accesibilidad.

- **`app/`** — SUT: Spring Boot 3 + Thymeleaf + H2 en memoria.
- **`qa/`** — Framework de automatización: Selenium WebDriver + REST Assured + axe-core, con reportes Allure.
- **`.github/workflows/ci.yml`** — pipeline CI que arranca el SUT y ejecuta toda la suite.

Objetivo del proyecto: demostrar un flujo completo de QA automation portable — SUT
real, suite de pruebas que no depende de apps de terceros y CI reproducible.

---

## Stack

| Capa              | Tecnología                                          |
| ----------------- | --------------------------------------------------- |
| Lenguaje          | Java 21                                             |
| SUT               | Spring Boot 3.5.3, Thymeleaf, Spring Data JPA, H2   |
| Framework          | Selenium 4.25, WebDriverManager 5.9, JUnit 5.11, REST Assured 5.5, axe-core 4.10 |
| Reportes          | Allure 2.30 (commandline) + allure-junit5           |
| CI                | GitHub Actions (ubuntu-latest, Temurin 21)          |
| Build             | Maven 3.9 (wrapper `mvnw`)                          |

---

## Estructura

```
qa-ecommerce-portfolio/
├─ app/                          # SUT: tienda e-commerce
│  └─ src/main/
│     ├─ java/com/sara/store/
│     │  ├─ catalog/             # Producto, repositorio, Home (MVC) y API /api/products
│     │  ├─ cart/                # Carrito en sesión HTTP, controllers MVC + API /api/cart
│     │  ├─ order/               # Pedido simulado, controllers MVC + API /api/orders
│     │  └─ config/              # DataSeeder (6 productos), endpooint de reset de datos
│     └─ resources/
│        ├─ templates/           # Vistas Thymeleaf con data-testid
│        └─ static/
│           ├─ css/              # Estilos
│           └─ images/products/  # Imagenes SVG de los productos
├─ qa/                           # Framework de pruebas
│  ├─ src/main/java/com/sara/qa/
│  │  ├─ config/                 # Config por propiedades/env (override con -D o QA_*)
│  │  ├─ driver/                 # DriverFactory + DriverManager (chrome/edge/firefox)
│  │  ├─ pages/                  # Page Object Model (BasePage, Home, Cart, Checkout, Order…)
│  │  ├─ api/                    # StoreApiClient (REST client con sesión aislada)
│  │  └─ utils/                  # ScreenshotUtils (captura en fallos)
│  └─ src/test/java/com/sara/qa/
│     ├─ api/                    # CatalogApiTests, CartApiTests, OrderApiTests
│     ├─ ui/                     # HomePageTests, CartFlowTests, CheckoutUiTests
│     ├─ a11y/                   # AccessibilityTests (axe-core)
│     └─ base/                   # BaseTest / BaseApiTest (setup y teardown)
├─ scripts/                      # start-app.ps1, run-tests.ps1, stop-app.ps1, abrir-app.cmd, parar-app.cmd
└─ .github/workflows/ci.yml      # CI
```

---

## Requisitos

- JDK 17+ (el proyecto compila con `--release 21`; recomendado Temurin 21).
- Un navegador Chrome, Edge o Firefox (WebDriverManager descarga el driver automáticamente).
- Maven no es obligatorio: se incluye el wrapper `mvnw`.

---

## Arrancar el SUT

Desde IntelliJ: ejecutar `com.sara.store.StoreApplication`.

Desde terminal (requiere `JAVA_HOME` apuntando a un JDK 17+):

```powershell
powershell -ExecutionPolicy Bypass -File scripts\start-app.ps1   # compila (si falta), arranca y espera /actuator/health
# SUT disponible en http://localhost:8080
powershell -ExecutionPolicy Bypass -File scripts\stop-app.ps1    # se detiene leyendo el PID guardado
```

Datos: al arrancar se siembran 6 productos. `POST /api/test/reset` (endpoint de prueba)
borra pedidos anteriores.

### Endpoints del SUT

| Método | Ruta                          | Descripción                          |
| ------ | ----------------------------- | ------------------------------------ |
| GET    | `/`                           | Catálogo (Thymeleaf)                 |
| GET    | `/api/products`               | Lista de productos                   |
| GET/POST/DELETE | `/api/cart...`         | Estado del carrito de la sesión      |
| POST   | `/api/orders`                 | Crear pedido desde el carrito        |
| GET    | `/api/orders/{número}`        | Recuperar pedido                     |
| POST   | `/api/test/reset`             | Reset de datos de prueba             |

---

## Ejecutar la suite

El SUT debe estar corriendo en `http://localhost:8080` (`run-tests.ps1` lo arranca solo si no está).

También hay accesos de doble clic: `abrir-app.cmd` (arranca la app si no está y abre el navegador) y `parar-app.cmd` (la detiene).

```powershell
# Todo (API + UI + accesibilidad)
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1

# Solo un grupo; headless; otro navegador
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1 -Groups ui
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1 -Groups api -Headless
powershell -ExecutionPolicy Bypass -File scripts\run-tests.ps1 -Groups ui -Browser edge

# Equivalente con Maven
mvnw -f qa/pom.xml test                                   # suite completa
mvnw -f qa/pom.xml test -Dtest.groups=ui                  # solo UI
mvnw -f qa/pom.xml test -Dheadless=true -Dbrowser=chrome  # overrides de config
```

> Nota: en PowerShell, escribe `-Dtest.groups=ui` entre comillas (`"-Dtest.groups=ui"`),
> porque PS 5.1 parte el token por el punto.

### Configuración (`qa/src/test/resources/config.properties`)

| Propiedad        | Valor por defecto                  | Override        |
| ---------------- | ---------------------------------- | --------------- |
| `app.baseUrl`    | `http://localhost:8080`            | `-Dapp.baseUrl` / `QA_APP_BASEURL` |
| `browser`        | `chrome`                           | `-Dbrowser` / `QA_BROWSER` |
| `headless`       | `false`                            | `-Dheadless=true` / `QA_HEADLESS` |
| `timeout.seconds`| `10`                               | `-Dtimeout.seconds` / `QA_TIMEOUT_SECONDS` |

---

## Cobertura de la suite

| Suite                    | Etiqueta | Qué verifica                                                        |
| ------------------------ | -------- | ------------------------------------------------------------------- |
| `CatalogApiTests`        | `api`    | Catálogo: 6 productos, campos esperados, producto inexistente → 404 |
| `CartApiTests`           | `api`    | Carrito: totales, quitar/vaciar, producto inexistente → 404, **aislamiento entre sesiones** |
| `OrderApiTests`          | `api`    | Pedido desde carrito, carrito vacío → 400, validación de campos, pedido inexistente → 404, reset de datos |
| `HomePageTests`          | `ui`     | Home muestra el catálogo, cada producto muestra su imagen, añadir incrementa el contador, navegación al carrito |
| `CartFlowTests`          | `ui`     | Flujo E2E completo (catálogo → carrito → checkout → confirmación), quitar recalcula total |
| `CheckoutUiTests`        | `ui`     | Validación del formulario (nombre/email) y redirect con carrito vacío |
| `AccessibilityTests`     | `a11y`   | axe-core sobre Home, Carrito y Checkout: **falla solo ante violaciones CRÍTICAS** WCAG A/AA |

---

## Decisiones de diseño del framework

- **SUT propio** en vez de una app demo externa: los tests son deterministas y el proyecto
  demuestra que también sabes construir el sistema bajo prueba.
- **Page Object Model** (`pages/`) sobre Spring MVC (transferencias de formulario clásicas,
  no SPA): `BasePage` centraliza los *waits* explícitos y helpers.
- **`data-testid` como contrato** entre vistas y tests: localizadores estables que no se
  rompen al cambiar nombres de clase CSS o estructura visual.
- **Sesiones HTTP aisladas por test**: el carrito vive en la sesión HTTP; el
  `StoreApiClient` gestiona su propio *cookie jar* explícito para que cada test (y cada
  llamada REST) reutilice la sesión, sin estado compartido entre tests.
- **Esperas explícitas** siempre (WebDriverWait). Nada de `sleep` duro: se espera por
  *staleness*, visibilidad o *clickable* de los elementos afectados, lo que elimina
  flakiness típica de navegación por formularios.
- **Config por propiedades y variables de entorno** con fallback: la misma suite corre
  local (headless=false) y en CI (headless=true) sin tocar código.
- **Accesibilidad como parte del pipeline**: axe-core con tags WCAG A/AA y política de
  fallo solo ante impacto crítico (las violaciones menores se reportan como attachment).

---

## CI / Reportes

El workflow `ci.yml` (push a `main`, pull requests, manual):

1. `actions/setup-java` (Temurin 21, cache de Maven).
2. Arranca el SUT en background y espera `/actuator/health` (hasta ~3 min).
3. `mvn test` (headless) contra el SUT real.
4. Sube `qa/target/allure-results` como artefacto siempre (`if: always()`).
5. En `main`, publica el reporte Allure en GitHub Pages (history-trend incluido).

Reporte local:

```powershell
mvnw -f qa/pom.xml io.qameta.allure:allure-maven:3.1.0:report
# Resultados: qa/target/allure-results  •  Reporte: qa/target/allure-report/index.html
```

---

## Roadmap / extensiones naturales

- Conteinerizar el SUT (Docker) para que CI y local compartan el mismo entorno.
- Reintentos automáticos de tests flaky en CI (surefire rerun).
- Generación de capturas/screenshots como attachment de Allure en cada fallo (ya hay utilidad `ScreenshotUtils`).
- Más capas de pruebas: seguridad básica (headers, rate-limit), rendimiento (JMeter/k6) y pruebas de contrato.