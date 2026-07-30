# MiniTêxtil - ERP

## Branch
Padrão de criação de branches


- ***feat*** melhorias e adição
---
- ***bugfix*** correções
---
- ***chore*** atualização de pacotes
---


## Deploy

Para rodar em desenvolvimento:
```
docker compose -f docker-compose.dev.yml watch
```

Criação de um arquivo ***.env*** com as variáveis
```env
DB_NAME=nome
DB_USER=user
DB_PASS=senha
```
