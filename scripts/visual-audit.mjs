import { chromium } from 'playwright';
import { mkdir } from 'node:fs/promises';
const colors=[
['paper','#faf9f5','#f0f1e9','#e9eddf','#2b302b','#e9785d'],
['tide','#f4f8fa','#e8f0f4','#ddeaf2','#293742','#3868ac'],
['clay','#fbf6ef','#f3eade','#f0dfce','#40352d','#ae503b'],
['olive','#f6f8f0','#eaf0df','#e0e8cf','#30392b','#677c3c'],
['midnight','#151b20','#10161b','#24313a','#eef2f0','#fa9077'],
['forest','#17221e','#111c18','#25392f','#eef5ec','#a6d2a0'],
['espresso','#211c1a','#191513','#382c26','#f7eee5','#e6a17b'],
['slate','#19232b','#121b22','#293c48','#eef4f6','#83c2d7'],
['sand','#f8f3ea','#eee5d8','#e9ddc9','#3b342e','#b16d4d'],
['rose','#fbf5f4','#f2e7e6','#efddda','#403334','#bd6e72'],
['obsidian','#101114','#090a0c','#24262b','#f3f3f4','#c4c8d2'],
['amethyst','#1c1726','#15101e','#30273f','#f2ebfa','#c4a2f0'],
['burgundy','#24171d','#1b1116','#3b2530','#f9ebef','#e4a0ae'],
['ocean','#101d2a','#0b1520','#20374b','#edf7fb','#78d0cb'],
['graphite','#1d2022','#15181a','#303638','#f2f5f3','#9ad9c1'],
['copper','#221d1a','#181411','#382d25','#f8f0e9','#d8a174'],
['abyss','#0a0e13','#070a0e','#17202b','#e9f1f7','#79a9d2'],
['blackout','#090909','#050505','#1b1b1b','#f2f2f2','#a9adb5'],
['eclipse','#11100e','#0b0a09','#262219','#f6f0e5','#c8a66b'],
['noir','#0e0e10','#08080a','#222025','#f2eef0','#d77b85'],
['deep-space','#0c0c18','#070711','#1c1b30','#eeecfa','#9b96d6'],
['evergreen','#0b1512','#07100d','#1c2b25','#ecf6ef','#87bca4']];
const browser=await chromium.launch({headless:true,args:['--no-sandbox']});
await mkdir('visual-artifacts',{recursive:true});
const failures=[];
for(const [name,paper,side,banner,ink,accent] of colors){
 for(const [width,height,label] of [[1440,900,'desktop'],[390,844,'mobile']]){
  const page=await browser.newPage({viewport:{width,height}});
  const errors=[];page.on('pageerror',e=>errors.push(e.message));
  await page.addInitScript(theme=>{localStorage.setItem('meu-ritmo-lite:appearance-cache',JSON.stringify(theme));}, {theme:name,paper,side,banner,ink,accent});
  await page.route('https://meuritmo.net/api/appearance-sync',route=>route.fulfill({status:200,headers:{'Access-Control-Allow-Origin':'*','Content-Type':'application/json'},body:JSON.stringify({theme:name,paper,side,banner,ink,accent})}));
  try{
   await page.goto('http://127.0.0.1:8765/',{waitUntil:'networkidle',timeout:20000});
   const actual=await page.evaluate(()=>({theme:document.documentElement.dataset.liteTheme,paper:getComputedStyle(document.documentElement).getPropertyValue('--paper').trim(),overflow:document.documentElement.scrollWidth>window.innerWidth+3}));
   if(actual.theme!=='linked'||actual.paper.toLowerCase()!==paper||actual.overflow||errors.length)failures.push({name,label,actual,errors});
   await page.screenshot({path:`visual-artifacts/${name}-${label}.png`,fullPage:true});
  }catch(e){failures.push({name,label,error:String(e)})}
  await page.close();
 }
}
await browser.close();
console.log(JSON.stringify({screenshots:44,failures},null,2));
if(failures.length)process.exitCode=1;
