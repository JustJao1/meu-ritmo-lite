# Meu Ritmo Lite

Aplicativo Android offline, sem login, planos ou comunidade. Desenvolvido por João Victor.

[Baixar APK de desenvolvimento](https://github.com/JustJao1/meu-ritmo-lite/releases/tag/v0.1.1-dev).

O diretório dist é a página de distribuição. Publique em Cloudflare Workers Static Assets com `npx wrangler@4.92.0 deploy` ou sirva os arquivos dist em uma hospedagem estática. O link Projeto Meu Ritmo leva ao repositório enquanto o novo endereço público não é configurado. Troque esse link pelo domínio real após validar a publicação do aplicativo principal.

app-web contém a interface embarcada; android contém alarmes nativos individuais, parar e adiar, restauração após reinício e integração com agenda mediante confirmação no sistema. Dados permanecem no aparelho. O código, recursos embarcados e o APK não foram alterados pela migração de hospedagem.

O APK v0.1.1-dev é uma versão em desenvolvimento e ainda requer testes em aparelhos Android reais. Não há promessa de alarme com aparelho desligado, parada forçada ou restrições do sistema. Guarde backups e preserve a chave de assinatura privada fora do repositório.
