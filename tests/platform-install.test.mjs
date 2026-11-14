import test from 'node:test';import assert from 'node:assert/strict';import {readFileSync} from 'node:fs';
const read=p=>readFileSync(new URL('../'+p,import.meta.url),'utf8');
test('Android and iPhone choices exist on the Lite landing page',()=>{const html=read('dist/index.html');assert.match(html,/id="instalar"/);assert.match(html,/id="iphone-guide"/);assert.match(html,/id="iphone-steps"/);assert.match(html,/https:\/\/meuritmo\.net\/app/);assert.match(html,/meu-ritmo-app\/releases\/download\/v1\.0\.0\/Meu-Ritmo-1\.0\.0\.apk/);assert.match(html,/Safari/);assert.match(html,/Adicionar à Tela de Início/);});
test('iPhone help button toggles guidance and preserves ARIA state',()=>{const js=read('dist/handoff.js');assert.match(js,/iphoneGuide\.addEventListener\('click'/);assert.match(js,/aria-expanded/);assert.match(js,/iphoneSteps\.hidden/);});
test('Installation layout adapts to mobile screens',()=>{const css=read('dist/download.css');assert.match(css,/\.installrows\{border-top/);assert.match(css,/@media\(max-width:600px\)/);});

test('return links preserve authenticated app destination and APP badge',()=>{const html=read('dist/index.html');assert.match(html,/class="appbadge"/);assert.match(html,/href="https:\/\/meuritmo\.net\/app">Voltar ao site/);assert.doesNotMatch(html,/href="https:\/\/meuritmo\.net\/">Voltar ao site/);});
