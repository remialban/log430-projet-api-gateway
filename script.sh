#!/bin/bash
# Liste des dépôts à traiter
# Format : "nom_dossier url_du_repo"
repos=(
  "projet1 https://github.com/remialban/log430-projet-api-gateway"
  "projet2 https://github.com/remialban/log430-orojet-transactions"
  "projet3 https://github.com/remialban/log430-projet-users"
)

# Dossier racine où les dépôts seront placés
base_dir="./"

# On se place dans le dossier de travail
cd "$base_dir" || exit 1

# Boucle sur chaque dépôt
for entry in "${repos[@]}"; do
  # Découper nom_dossier et URL
  read -r folder repo_url <<< "$entry"

  echo "=== Traitement de $folder ==="

  # Supprimer l'ancien dossier s'il existe
  if [ -d "$folder" ]; then
    echo "Suppression de $folder..."
    rm -rf "$folder"
  fi

  # Cloner le dépôt
  echo "Clonage de $repo_url dans $folder..."
  git clone "$repo_url" "$folder" || { echo "Erreur de clonage pour $folder"; continue; }

  # Lancer docker-compose
  cd "$folder" || continue
  if [ -f docker-compose.yml ]; then
    echo "Arret des conteneurs..."
    docker compose down

    echo "Démarrage du docker-compose..."

    docker compose up --build -d
  else
    echo "Aucun docker-compose.yml trouvé dans $folder."
  fi
  cd "$base_dir" || exit 1

  echo "=== Terminé pour $folder ==="
  echo
done
