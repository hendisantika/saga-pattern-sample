-- one schema per service to mimic "database per service"
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS payment_service;
CREATE SCHEMA IF NOT EXISTS inventory_service;
CREATE SCHEMA IF NOT EXISTS shipping_service;
CREATE SCHEMA IF NOT EXISTS saga_orchestrator;
