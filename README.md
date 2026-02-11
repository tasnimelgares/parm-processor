# PARM – Processeur pédagogique (Architecture & Microprocesseur)

## Contexte académique

Projet réalisé en **SI3 – Semestre 5** (2025-2026) à Polytech Nice-Sophia,
dans le cadre du module d’architecture des systèmes.

Il s’agit d’un **travail de groupe réalisé à 4 étudiants**.

La version publique correspond à un **snapshot stable du projet**.
Le dépôt original Classroom reste privé conformément aux consignes pédagogiques.

---

## Description du projet

Le projet PARM consiste à concevoir et implémenter un **processeur pédagogique 32 bits**, inspiré d’une architecture de type ARM Cortex-M0 simplifiée.

Les objectifs étaient de :

* concevoir les différents composants matériels du processeur,
* implémenter une unité arithmétique et logique (ALU),
* gérer les registres et les flags,
* développer un contrôleur d’instructions,
* exécuter des programmes assembleur,
* interfacer le processeur avec des périphériques via MMIO.

L’implémentation matérielle a été réalisée sous **Logisim**.

---

## Architecture implémentée

Le processeur comprend :

* Une ALU 32 bits
* Une banque de registres (R0–R7, SP, PC, APSR)
* Une unité de gestion des flags (N, Z, C, V)
* Un contrôleur d’instructions
* Un mécanisme de branchement conditionnel
* Une gestion mémoire ROM / RAM
* Une interface MMIO

L’ensemble forme un CPU fonctionnel capable d’exécuter des programmes assembleur simples.

---

## Jeu d’instructions supporté

Le processeur implémente notamment :

* Instructions arithmétiques : ADD, SUB, ADC, SBC
* Instructions logiques : AND, ORR, EOR
* Comparaisons : CMP, TST
* Décalages : LSL, LSR, ROR
* Branches conditionnelles et inconditionnelles
* Instructions de chargement et de stockage

---

## Organisation du dépôt

* `logisim_project/` : circuits du processeur (ALU, contrôleur, CPU complet)
* `code_asm/` : programmes assembleur de test
* `code_c/` : programmes C compatibles avec l’architecture
* `parseur/` : outils liés à l’assemblage / désassemblage
* `tests_vectors/` : jeux de tests

---

## Objectifs pédagogiques

* Comprendre le fonctionnement interne d’un processeur
* Concevoir une architecture matérielle modulaire
* Implémenter une logique de contrôle cohérente
* Tester et valider un CPU via des programmes assembleur

---

## Technologies utilisées

* Logisim
* Assembleur personnalisé
* C
* Git / GitHub

---

## Remarques

Le projet a été réalisé à partir de la documentation officielle fournie dans le cadre du module.
Cette documentation n’est pas incluse dans cette version publique.

Le projet s’appuie sur une base pédagogique distribuée sous licence MIT (voir fichier `LICENSE`).

