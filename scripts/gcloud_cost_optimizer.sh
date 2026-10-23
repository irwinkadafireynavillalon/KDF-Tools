#!/data/data/com.termux/files/usr/bin/bash
# ==============================================================================
# KDF-STUDIO · OPTIMIZADOR Y BLINDAJE DE COSTOS EN GOOGLE CLOUD
# Desactiva APIs innecesarias, costosas y de Big Data preservando Gemini y Core
# ==============================================================================

GCLOUD_BIN="/data/data/com.termux/files/home/google-cloud-sdk/bin/gcloud"
PROJECT_ID="kdf-studio"

if [ ! -f "$GCLOUD_BIN" ]; then
    echo "❌ Error: gcloud no encontrado en $GCLOUD_BIN"
    exit 1
fi

echo "=========================================================="
echo "🛡️  INICIANDO SANEAMIENTO DE APIS EN GOOGLE CLOUD: $PROJECT_ID"
echo "=========================================================="

# Lista de APIs costosas / innecesarias para una suite personal/web
APIS_TO_DISABLE=(
    "bigquery.googleapis.com"
    "bigqueryconnection.googleapis.com"
    "bigquerydatapolicy.googleapis.com"
    "bigquerydatatransfer.googleapis.com"
    "bigquerymigration.googleapis.com"
    "bigqueryreservation.googleapis.com"
    "bigquerystorage.googleapis.com"
    "analyticshub.googleapis.com"
    "dataplex.googleapis.com"
    "dataform.googleapis.com"
    "sql-component.googleapis.com"
    "appoptimize.googleapis.com"
    "apptopology.googleapis.com"
)

for api in "${APIS_TO_DISABLE[@]}"; do
    echo "[-] Deshabilitando API costosa: $api..."
    $GCLOUD_BIN services disable "$api" --project="$PROJECT_ID" --quiet 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "    ✓ Deshabilitada con éxito."
    else
        echo "    * Ya deshabilitada o no requerida."
    fi
done

echo ""
echo "=========================================================="
echo "🔍 VERIFICANDO APIS CRÍTICAS ACTIVAS (GEMINI, STORAGE, IAM)"
echo "=========================================================="
$GCLOUD_BIN services list --enabled --project="$PROJECT_ID" --filter="name:(generativelanguage OR storage OR iam OR apikeys OR logging)"

echo ""
echo "🛡️ Saneamiento de Google Cloud completado con éxito."
