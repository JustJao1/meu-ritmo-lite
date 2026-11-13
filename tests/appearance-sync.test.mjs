import test from 'node:test';import assert from 'node:assert/strict';import {readFileSync} from 'node:fs';
const js=readFileSync(new URL('../app-web/app.js',import.meta.url),'utf8');
const css=readFileSync(new URL('../app-web/lite-adapter.css',import.meta.url),'utf8');
const html=readFileSync(new URL('../app-web/index.html',import.meta.url),'utf8');
test('Lite has no independent theme selector',()=>{assert.doesNotMatch(js,/lite-theme-select|THEMES=\['original'/);assert.match(js,/appearance-link-form/);assert.match(js,/unlinkappearance/);});
test('linked theme has offline cache and limited endpoint',()=>{assert.match(js,/THEME_CACHE_KEY/);assert.match(js,/localStorage\.setItem\(THEME_CACHE_KEY/);assert.match(js,/https:\/\/meuritmo\.net\/api\/appearance-sync/);assert.match(html,/connect-src 'self' https:\/\/meuritmo\.net/);assert.match(css,/data-lite-theme="linked"/);});
const java=readFileSync(new URL('../android/app/src/main/java/br/com/joaovictor/meuritmolite/MainActivity.java',import.meta.url),'utf8');
test('Android permits only appearance endpoint and preflight to leave the offline sandbox',()=>{assert.match(java,/"meuritmo.net"\.equals\(u\.getHost\(\)\)/);assert.match(java,/"\/api\/appearance-sync"\.equals\(u\.getPath\(\)\)/);assert.match(java,/"OPTIONS"\.equalsIgnoreCase\(r\.getMethod\(\)\)/);});
