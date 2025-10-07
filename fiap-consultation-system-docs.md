# FIAP Consultation System - Documentação Técnica

## 📋 Visão Geral

O **FIAP Consultation System** é uma solução completa para gerenciamento de consultas médicas, composta por dois microserviços que trabalham em conjunto através de mensageria Apache Kafka.

### Componentes do Sistema
- **Consultation Service**: API REST para gerenciamento de consultas e usuários
- **Consultation Consumer**: Consumidor Kafka para processamento de eventos e lembretes

---

## 🏗️ Arquitetura do Sistema

### Diagrama de Arquitetura
![Arquitetura do Sistema](https://github.com/user-attachments/assets/9f291259-15ed-4dbb-b179-537a09da291d)

### Fluxo de Dados
1. **Frontend** → **Consultation Service** (API REST)
2. **Consultation Service** → **Apache Kafka** (Eventos de agendamento)
3. **Apache Kafka** → **Consultation Consumer** (Processamento assíncrono)
4. **Consultation Consumer** → **Email Service** (Notificações)
5. **Scheduled Job** → **Database** (Lembretes diários)

### Tecnologias Utilizadas
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Apache Kafka** - Message broker
- **PostgreSQL** - Banco de dados
- **JWT** - Autenticação
- **Docker** - Containerização
- **GitHub Actions** - CI/CD

---

## 🔧 Consultation Service (API)

### Responsabilidades
- Gerenciamento de usuários (médicos, pacientes, enfermeiros)
- CRUD de consultas médicas
- Autenticação e autorização JWT
- Publicação de eventos no Kafka

### Principais Endpoints

#### Usuários (`/api/users`)
| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| `GET` | `/api/users` | Lista todos os usuários | ✅ |
| `POST` | `/api/users` | Cria novo usuário | ❌ |
| `GET` | `/api/users/{id}` | Busca usuário por ID | ✅ |
| `PUT` | `/api/users/{id}` | Atualiza usuário | ✅ |
| `DELETE` | `/api/users/{id}` | Desativa usuário | ✅ |
| `PATCH` | `/api/users/{id}/change-password` | Altera senha | ✅ |

#### Consultas (`/api/consultations`)
| Método | Endpoint | Descrição | Permissões |
|--------|----------|-----------|------------|
| `GET` | `/api/consultations` | Lista consultas | MEDIC, NURSE |
| `POST` | `/api/consultations` | Cria consulta | MEDIC, NURSE |
| `PUT` | `/api/consultations/{id}` | Atualiza consulta | MEDIC, NURSE |
| `PATCH` | `/api/consultations/{id}/confirm` | Confirma consulta | PATIENT |
| `PATCH` | `/api/consultations/{id}/cancel` | Cancela consulta | PATIENT |

### Configuração
- **Porta**: 8080
- **Database**: PostgreSQL (consultation-db)
- **Kafka Topic**: `consultation.service.request.topic.scheduling`

---

## 📨 Consultation Consumer

### Responsabilidades
- Consumo de mensagens Kafka de agendamento
- Processamento de eventos de consulta
- Envio de lembretes automáticos
- Simulação de envio de emails

### Funcionalidades Principais

#### Consumer Kafka
- **Tópico**: `consultation.service.request.topic.scheduling`
- **Group ID**: `consultation.service-scheduling-consumer-1`
- **Processamento**: Assíncrono com simulação de email

#### Job Agendado
- **Horário**: Diariamente às 23:00 (horário de Brasília)
- **Função**: Busca consultas do dia seguinte
- **Ação**: Envia lembretes para pacientes
- **Cron Expression**: `0 0 23 * * *`

### Configuração
- **Porta**: 8081
- **Database**: PostgreSQL (consultation-db)
- **Timezone**: America/Sao_Paulo

---

## 🗄️ Modelo de Dados

### Usuário (Users)
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "typeUserRole": "MEDIC|PATIENT|NURSE|ADMIN",
  "createdAt": "datetime",
  "lastUpdate": "datetime"
}
```

### Consulta (Consultation)
```json
{
  "id": "uuid",
  "medicId": "uuid",
  "patientId": "uuid",
  "startDate": "datetime",
  "finalDate": "datetime",
  "status": "SCHEDULED|CONFIRMED|CANCELLED|COMPLETED",
  "description": "string"
}
```

---

## ⚡ Principais Diferenciais

### 1. Arquitetura de Microserviços
- **Separação de responsabilidades**: API e processamento assíncrono
- **Escalabilidade independente**: Cada serviço pode escalar conforme demanda
- **Resiliência**: Falha em um serviço não afeta o outro

### 2. Mensageria Assíncrona
- **Apache Kafka**: Processamento de eventos em tempo real
- **Desacoplamento**: Serviços não dependem diretamente um do outro
- **Confiabilidade**: Garantia de entrega de mensagens

### 3. Agendamento Inteligente
- **Job automático**: Execução diária para lembretes
- **Timezone específico**: Configurado para horário de Brasília
- **Consultas do dia seguinte**: Busca otimizada por período

### 4. Segurança Robusta
- **JWT Authentication**: Tokens seguros para autenticação
- **Autorização por roles**: Diferentes permissões por tipo de usuário
- **Validação de dados**: Validações completas em todas as camadas

### 5. Observabilidade
- **Health Checks**: Monitoramento da saúde dos serviços
- **Logs estruturados**: Rastreabilidade completa das operações
- **Métricas**: Prometheus e Actuator para monitoramento

---

## 🚀 Execução e Deploy

### Pré-requisitos
- Java 21
- Docker e Docker Compose
- PostgreSQL
- Apache Kafka

### Execução Local

#### 1. Infraestrutura
```bash
# No projeto consultation-service
docker-compose up -d postgres apache-kafka kafka-ui
```

#### 2. Consultation Service
```bash
cd fiap-consultation-service
./gradlew bootRun
# Acesso: http://localhost:8080
```

#### 3. Consultation Consumer
```bash
cd fiap-consultation-consumer
./gradlew bootRun
# Acesso: http://localhost:8081
```

### Variáveis de Ambiente
| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5432` |
| `DB_SCHEMA` | Nome do banco | `consultation-db` |
| `BD_USER` | Usuário do banco | `postgres` |
| `BD_PASS` | Senha do banco | `root` |

---

## 🧪 Testes e Qualidade

### Consultation Service
- **60 testes unitários** em 10 classes
- **Cobertura completa** de controllers, services e domínio
- **Testes de integração** com Spring Boot Test

### Consultation Consumer
- **35 testes unitários** em 7 classes
- **Testes de consumer Kafka** com simulação
- **Testes de jobs agendados** com validação de execução
- **Testes de repository** com H2 em memória

### CI/CD
- **GitHub Actions** configurado para ambos os projetos
- **Execução automática** de testes em push/PR
- **Build verification** antes do deploy

---

## 📊 Monitoramento

### Health Checks
- **Consultation Service**: `http://localhost:8080/actuator/health`
- **Consultation Consumer**: `http://localhost:8081/actuator/health`

### Interfaces de Monitoramento
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Kafka UI**: `http://localhost:8085`
- **PostgreSQL**: Porta 5432

### Logs Importantes
```bash
# Consumer Kafka
===== MESSAGE RECEIVED =====
===== SIMULANDO ENVIO DE EMAIL =====

# Job Agendado
===== SCHEDULING JOB =====
===== LEMBRETE DE CONSULTA =====
===== FINALIZANDO JOB =====
```

---

## 🔄 Fluxo Completo do Sistema

### 1. Criação de Consulta
1. Frontend chama `POST /api/consultations`
2. Consultation Service valida e persiste
3. Evento enviado para Kafka
4. Consumer processa e simula email de confirmação

### 2. Lembretes Automáticos
1. Job executa diariamente às 23:00
2. Busca consultas do dia seguinte no banco
3. Processa cada consulta encontrada
4. Simula envio de lembrete por email

### 3. Gestão de Usuários
1. Cadastro de médicos, pacientes e enfermeiros
2. Autenticação via JWT
3. Autorização baseada em roles
4. Operações CRUD completas

---

## 📞 Informações de Contato

- **Repositórios**:
  - [Consultation Service](https://github.com/itmoura/fiap-consultation-service)
  - [Consultation Consumer](https://github.com/itmoura/fiap-consultation-consumer)
- **Desenvolvedor**: Italo Moura
- **Instituição**: FIAP
- **Tecnologia**: Spring Boot + Apache Kafka

---

*Documentação técnica do sistema de consultas médicas FIAP - Versão 1.0*
