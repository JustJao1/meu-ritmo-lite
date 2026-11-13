# Lançamento conjunto — aparência do Meu Ritmo

Não publicar isoladamente. A integração depende do endpoint `/api/appearance-sync` no domínio `https://meuritmo.net`.

## Ordem na janela única de lançamento
1. Validar os dois branches e revisar as diferenças para `main`.
2. Garantir que a migração D1 `0009_appearance_links.sql` seja aplicada pelo deploy do site principal.
3. Publicar o Meu Ritmo normal, confirmar login e gerar uma chave em **Meu estilo → Aparência**.
4. Recompilar o APK Lite com `scripts/build-android.py` usando a chave de assinatura legítima do projeto. O APK já distribuído em `dist/releases` não contém as alterações.
5. Testar no Android real: vinculação, tema claro/escuro, 22 paletas, retorno da rede, funcionamento offline, alarmes, notas, agenda, gastos e restauração de backup.
6. Atualizar a versão, checksum, download e publicação do Lite apenas após os testes; validar que o APK é uma atualização instalável sobre o anterior.
7. Confirmar que a aparência é a única informação compartilhada. Revogar a chave e confirmar que o Lite mantém somente a última aparência armazenada.

## Verificações já automatizadas
- Meu Ritmo: `npm run typecheck`, `npm test`, `npm run build` no workflow de verificação.
- Lite: `node --test tests/*.mjs` e integridade do APK antigo. O teste do APK antigo **não valida o novo APK**.

## Pendências antes da publicação
- Testes visuais reais em desktop/mobile e em dispositivo Android.
- Compilação, assinatura, checksum e instalação de uma nova versão do APK.
- Teste de integração autenticada contra ambiente publicado com banco D1 migrado.
- Aprovação final para publicar os dois projetos juntos.

A branch de auditoria não deve ser mesclada automaticamente à `main`.
