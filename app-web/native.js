export const native=typeof window.AndroidHost?.sync==='function';let current={exact:false,notifications:false,scheduled:[]},timer;
export function nativeStatus(){if(native)try{current={...current,...JSON.parse(AndroidHost.getStatus())}}catch{}return current}
export function syncNative(data){if(!native)return;clearTimeout(timer);const raw=JSON.stringify(data);timer=setTimeout(()=>AndroidHost.sync(raw),150)}
export function nativePermissions(){if(native)AndroidHost.requestPermissions()}
export function nativeTest(){if(native)AndroidHost.testAlarm()}
window.addEventListener('ritmo:native',e=>current=e.detail);

export function dismissNative(id,snooze=false){if(native)AndroidHost.dismissAlarm(id,snooze)}
