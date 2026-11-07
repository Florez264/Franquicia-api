# Persistencia como infraestructura como código

Esta carpeta describe MySQL DEV de Clever Cloud con su proveedor oficial de Terraform 3.0.0. Sustituye el ejemplo anterior de AWS, que no representaba el despliegue utilizado. El plan está fijado a `dev` y no se parametriza un plan de pago.

Alcance: aprovisionar una base nueva o adoptar la base ya creada. Las tablas se definen en `src/main/resources/schema.sql`; no las gestiona este recurso. El despliegue de la API sigue en Railway con `railway.json`. No se afirma que Terraform gestione Railway.

## Validación sin crear recursos

Requisitos: Terraform 1.7 o superior, menor que 2.0. La CI usa 1.13.0. Ejecuta desde esta carpeta:

```bash
terraform init -backend=false
terraform fmt -check -recursive
terraform validate
```

Estos comandos descargan el proveedor y validan los archivos. No requieren tokens de Clever Cloud ni crean servicios. El lockfile `.terraform.lock.hcl` sí se versiona; los directorios `.terraform`, los planes, tfvars reales y el estado no.

## Adoptar la base que ya está funcionando

### Si el plugin falla en Windows

Si `validate` falla al leer el esquema del plugin (por ejemplo un fallo TLS entre Terraform y el proveedor), utiliza Docker Desktop y el script de Linux desde la raíz del proyecto:

```powershell
$env:CC_ORGANISATION = 'user_TU_ID'
.\scripts\terraform-docker.ps1 init
.\scripts\terraform-docker.ps1 validate
.\scripts\terraform-docker.ps1 plan
.\scripts\terraform-docker.ps1 summary
```

El script usa Terraform 1.13.0, monta la sesión de Clever Tools en lectura y usa certificados públicos de confianza de Windows para las conexiones HTTPS. El archivo auxiliar de certificados se guarda en `%TEMP%\Franquicia-api-terraform`, fuera del proyecto. No desactiva TLS ni copia tokens al repositorio. `plan` solo guarda el plan en la carpeta Terraform. Requiere haber iniciado sesión con Clever Tools. `summary` muestra solo acciones e ID de importación, sin volcar el JSON sensible del plan.

Después de revisar que el resumen indique una sola importación sin modificaciones remotas, puedes registrarla con:

```powershell
.\scripts\terraform-docker.ps1 adopt
.\scripts\terraform-docker.ps1 plan
```

`adopt` comprueba que el plan guardado solo importe `clevercloud_mysql.franquicia` y que todas las acciones sean no-op antes de aplicar ese archivo. El plan posterior debe indicar que no hay cambios y ya no debe proponer una importación. Conserva el estado local para no volver a adoptar el mismo servicio.

No ejecutes un apply de creación sobre tu entorno actual. El modo predeterminado `adopt` requiere un ID existente y bloquea el plan si no lo proporcionas.

1. Instala Clever Tools y ejecuta `clever login` localmente. No compartas sus tokens. El proveedor reutiliza la sesión. Como alternativa admite `CLEVER_API_TOKEN` por entorno, o `CC_OAUTH_TOKEN` y `CC_OAUTH_SECRET`. Son credenciales de administración de Clever Cloud, distintas de la contraseña MySQL.
2. Define `CC_ORGANISATION` con el ID del espacio personal (`user_...`) u organización (`orga_...`). No guardes tokens en código.
3. Copia `terraform.tfvars.example` a `terraform.tfvars` y reemplaza el ID por el **mysql_...** que aparece en el dashboard de la base. Verifica nombre y región reales; el ejemplo usa Montreal (`mtl`).
4. Inicializa y revisa el plan:

```bash
terraform init
terraform plan -out=adoption.tfplan
```

Para una adopción sin cambios espera **1 to import, 0 to add, 0 to change, 0 to destroy**. Si propone modificaciones, compara con los ajustes existentes y corrige la configuración antes de continuar. `prevent_destroy` bloquea la destrucción y reemplazos mientras permanezca en la configuración; no protege frente a borrados manuales ni a retirar el recurso completo del código.

Solo después de revisar ese plan, aplica exactamente el archivo guardado:

```bash
terraform apply adoption.tfplan
terraform plan
```

El segundo plan debe indicar que no hay cambios. Esto registra la base existente en el estado sin recrearla ni mover datos. No es necesario cambiar las variables de Railway durante la adopción.

## Crear un entorno nuevo reproducible

En otra carpeta de trabajo y con estado independiente, configura `mode = "create"`, `existing_mysql_id = null`, un nombre diferente y una región donde DEV esté disponible. Ejecuta `terraform plan`, verifica que solo cree un MySQL DEV, y aplica tras revisar su disponibilidad y condiciones. El proveedor genera el nombre interno y las credenciales: no serán los mismos del entorno anterior.

Consulta `terraform output r2dbc_url` y los demás outputs para configurar Railway. Obtén la contraseña del dashboard, ejecútale el esquema SQL a la base nueva y configura el perfil cloud. No se emite la contraseña como output.

## Estado y entrega

El estado de Terraform puede incluir la contraseña generada por el proveedor aunque esté marcada como sensitive. Guárdalo localmente con acceso restringido y no publiques `terraform show`, tfstate o planes. Para trabajo en equipo utiliza un backend remoto protegido; este ejercicio no crea un backend de pago.

La CI solo ejecuta fmt/init/validate: no ejecuta plan, apply ni destroy contra tu cuenta y no requiere secretos del proveedor. Validar la sintaxis no equivale a haber importado/aprovisionado el entorno. La adopción solo estará completada cuando ejecutes el flujo autenticado y el plan posterior no muestre cambios.

Para la evaluación puedes explicar: «La persistencia MySQL está definida con Terraform y el proveedor oficial Clever Cloud. Se documentan creación y adopción por importación; Railway usa configuración de despliegue versionada». Añade evidencia del plan sin cambios tras adoptar, ocultando secretos, cuando esté disponible.

## Fuentes

- [Proveedor oficial de Clever Cloud](https://registry.terraform.io/providers/CleverCloud/clevercloud/latest/docs)
- [Guía de importación de recursos existentes](https://www.clever.cloud/blog/engineering/2026/11/03/terraform-import-blocks-existing-infrastructure/)
- [Import blocks de Terraform](https://developer.hashicorp.com/terraform/language/import)
