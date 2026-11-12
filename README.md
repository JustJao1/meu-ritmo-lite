# Meu Ritmo Lite para Android

[Download do APK e instruções](https://meu-ritmo-lite.tonedcamel16.chatgpt.site/) · [Meu Ritmo completo](https://meu-ritmo-organiza.tonedcamel16.chatgpt.site/)

Versões para baixar também ficam na seção Releases deste repositório.

Aplicativo offline em português, sem conta, planos ou comunidade. Mantém a identidade Papel do Meu Ritmo: cores, fontes, marca e ícones locais. A página `dist/` distribui o APK; a interface não é mais publicada como aplicativo de navegador.

## Organização do código

- `app-web/`: interface embarcada no APK, dados locais e regras de organização.
- `android/`: aplicação Android nativa Java, WebView restrito aos arquivos embarcados, AlarmManager, notificações, serviço de áudio e restauração de alarmes após reinício.
- `dist/`: página pública de download, APK experimental, hash e exportação dos dados da antiga versão web.
- `tests/`: regras JavaScript. `android/tests/`: recorrências nativas.

Hoje, agenda diária/semanal/mensal, rotina, gastos, notas, recados, lembretes e alarmes. Orçamento excedido fica vermelho até revisão com justificativa; a justificativa não altera o valor. Um novo gasto ou mudança de orçamento invalida a revisão. Há edição, exclusão, desfazer ações simples, recorrências, busca, backups e três itens fixados. Sem dados fictícios iniciais.

## Alarmes e permissões

Cada alarme e lembrete com horário possui PendingIntent único e agendamento próprio no AlarmManager. Lembretes podem ativar alarme sonoro. Recorrência diária, semanal e mensal acompanha o fuso do aparelho; dia 31 se ajusta ao fim do mês. Cancelar, pausar ou editar um item cancela seu próprio agendamento. Parar e adiar dez minutos estão disponíveis em cada notificação e na tela de alarmes. Avisos simultâneos mantêm controles separados, com áudio em fila. O som dura até dois minutos. São aceitos até 200 avisos ativos e 2.000 registros no total.

Permissões solicitadas quando necessárias: notificações (Android 13+) e acesso especial a alarmes exatos (Android 12+). O estado aparece na interface, com teste de dez segundos. Se não autorizados, o item permanece salvo, mas não é agendado. Reinício, atualização e mudança de horário/fuso reconstroem os agendamentos. Os alarmes são do aplicativo no sistema Android, não entradas no aplicativo Relógio.

O calendário usa ACTION_INSERT: a pessoa revisa e confirma no aplicativo de agenda. Não lemos outros compromissos e não pedimos acesso amplo ao calendário. Backups usam seletor nativo, sem acesso amplo ao armazenamento. Não são solicitados câmera, microfone, localização ou contatos. As permissões técnicas de serviço em primeiro plano, wake lock, vibração e reinício permitem executar os avisos.

Não há alarme com aparelho desligado ou aplicativo em parada forçada. Volume, Não perturbe, canais bloqueados e restrições de fabricantes podem interferir. Não há contorno dessas escolhas do sistema.

## Dados e segurança

Dados no armazenamento privado do WebView, chave `meu-ritmo-lite:v1`, e apenas informações necessárias aos alarmes nas preferências privadas nativas. Backup automático Android desativado. Exportação explícita em JSON, restauração validada, exclusão completa com confirmação APAGAR cancela os agendamentos. Desinstalação ou limpeza dos dados remove os registros. Não há sincronização entre dispositivos.

Sem analytics, publicidade, SDK de terceiros, conta, senha ou chave de API. Conteúdo digitado é escapado; o bridge JavaScript é exposto apenas à origem local embarcada, requisições externas no WebView são bloqueadas e links HTTPS abrem no navegador externo. Debug do WebView desativado, tráfego sem TLS bloqueado. O site de distribuição pode manter logs técnicos da hospedagem.

A transição do site antigo preserva o localStorage e permite exportar. O novo service worker remove apenas os caches públicos antigos, sem apagar registros pessoais.

## Compilar

Requisitos: JDK 17 ou superior, SDK Android platform 36 e build-tools 36.0.0. Configure ANDROID_SDK_ROOT, ANDROID_KEYSTORE, ANDROID_KEY_ALIAS e ANDROID_KEYSTORE_PASSWORD no ambiente. Execute `python3 scripts/build-android.py`. O script copia app-web, compila com as ferramentas oficiais e verifica a assinatura. Alternativamente use Gradle 8.13 com o projeto Android. Nunca coloque chaves ou senhas no Git. Para atualizações, mantenha o mesmo identificador e chave de assinatura, e aumente versionCode/versionName.

A versão distribuída é 0.1.1-dev, minSdk 26 (Android 8), targetSdk 36. Assinatura de desenvolvimento, não destinada a lançamento em lojas. A chave privada não faz parte deste repositório. SHA-256 e tamanho constam em dist/release.json e devem coincidir com lib/android-release.json no site principal.

## Verificação realizada e pendências

Oito testes JavaScript passaram (datas, recorrências, gastos, backup, centavos, sobreposição, conteúdo textual e opção de alarme sonoro). Testes Java das recorrências passaram. APK compilado com ferramentas oficiais, zipalign e assinatura v2/v3 verificada. Página de distribuição e página de download avaliadas em navegador.

Não executado em aparelho/emulador Android. Antes de distribuição estável, testar instalação, armazenamento/reabertura, permissões concedidas e negadas, alarmes simultâneos, parar/adiar, tela bloqueada, segundo plano, reinício, timezone, atualização, restauração de backup e fabricantes diferentes. Não apresentar a compilação como validação desses cenários.

## Créditos

Desenvolvido por João Victor. Ícones Lucide ISC, licença em app-web/licenses. Open Sans local, licença Apache 2.0 em app-web/fonts. Sem imagens de bancos externos. A hospedagem Sites mantém fonte e versões Git próprias; não há credenciais no repositório.

Marca do Lite alinhada ao Meu Ritmo, com ® e LITE ao lado. Símbolo vetorial do ícone usa contornos de Noto Serif (OFL); licença em app-web/licenses/noto-fonts.txt. O cabeçalho do aplicativo mostra a marca também em celulares. O botão Conhecer o Meu Ritmo abre a página inicial em nova aba.
