Documentação Técnica - APLICATIVO 
PARA CORREÇÃO DE GABARITO 
1. Visão Geral do Projeto 
Este sistema foi desenvolvido para apoiar o gerenciamento de provas, gabaritos, respostas e 
notas de alunos em uma instituição de ensino. A aplicação permite que professores criem 
provas, associem gabaritos, acompanhem respostas dos alunos e consultem notas 
atribuídas automaticamente com base nas respostas fornecidas. 
2. Funcionalidades Implementadas 
• Cadastro e login de usuários com diferentes perfis (Administrador, Professor, Aluno); 
• Criação e listagem de provas com associação de gabaritos; 
• Resposta de provas por parte dos alunos, com correção automática baseada no gabarito 
no Painel de Professor; 
• Consulta de notas por professores, com filtro por prova, disciplina e turma; 
• Listagem geral de notas por professor autenticado; 
• Interface com validações, mensagens de erro e feedback ao usuário; 
• Integração com banco de dados relacional (MySQL) via Spring Data JPA; 
• Sistema de autenticação e autorização via Spring Security. 
• Consulta de notas para alunos autenticados; 
3. Tecnologias Utilizadas 
• Java 21 
• Spring Boot 3.5 
• Spring Security 
• Spring Data JPA 
• MySQL 9.2.0 
• Thymeleaf 
• Bootstrap 5 
• ModelMapper 
• Lombok 
4. Estrutura de Pacotes 
• controller: controladores MVC responsáveis pelas rotas e redirecionamento das views 
• service: regras de negócio e chamadas aos repositórios 
• model: entidades JPA que representam as tabelas do banco 
• repository: interfaces que fazem persistência de dados com Spring Data JPA 
• dto: classes de transporte de dados entre camadas 
5. Considerações Finais 
O projeto apresenta uma base sólida para gerenciamento de provas, com arquitetura MVC e 
integração robusta com banco de dados. O uso de boas práticas como DTOs, serviços e 
separação de responsabilidades proporciona manutenibilidade e evolução contínua do 
sistema.
