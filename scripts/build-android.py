#!/usr/bin/env python3
"""Build the offline Android APK with the official Android SDK, without Gradle downloads.
Signing key and passwords are read only from environment variables.
"""
import os,subprocess,shutil,zipfile,pathlib,sys
root=pathlib.Path(__file__).resolve().parents[1];android=root/'android';sdk=pathlib.Path(os.environ['ANDROID_SDK_ROOT']);tools=sdk/'build-tools/36.0.0';jar=sdk/'platforms/android-36/android.jar';out=android/'app/build/direct';out.mkdir(parents=True,exist_ok=True)
assets=android/'app/src/main/assets/site';shutil.rmtree(assets,ignore_errors=True);shutil.copytree(root/'app-web',assets)
def run(args,**kw):subprocess.run([str(a)for a in args],check=True,**kw)
run([tools/'aapt2','compile','--dir',android/'app/src/main/res','-o',out/'resources.zip'])
(out/'gen').mkdir(exist_ok=True)
run([tools/'aapt2','link','-o',out/'unsigned.apk','-I',jar,'--manifest',android/'app/src/main/AndroidManifest.xml','--min-sdk-version','26','--target-sdk-version','36','--version-code','2','--version-name','0.1.1-dev','--java',out/'gen','--auto-add-overlay','-A',android/'app/src/main/assets',out/'resources.zip'])
classes=out/'classes';shutil.rmtree(classes,ignore_errors=True);classes.mkdir();sources=list((android/'app/src/main/java').rglob('*.java'))+list((out/'gen').rglob('*.java'))
run(['javac','-source','8','-target','8','-Xlint:-options','-encoding','UTF-8','-classpath',jar,'-d',classes,*sources])
run(['jar','cf',out/'classes.jar','-C',classes,'.']);dex=out/'dex';dex.mkdir(exist_ok=True)
run([tools/'d8','--min-api','26','--lib',jar,'--output',dex,out/'classes.jar'])
with zipfile.ZipFile(out/'unsigned.apk','a',zipfile.ZIP_DEFLATED)as z:
 for f in dex.glob('*.dex'):z.write(f,f.name)
run([tools/'zipalign','-f','-p','4',out/'unsigned.apk',out/'aligned.apk'])
final=out/'meu-ritmo-lite-0.1.1-dev.apk'
run([tools/'apksigner','sign','--ks',os.environ['ANDROID_KEYSTORE'],'--ks-key-alias',os.environ.get('ANDROID_KEY_ALIAS','development'),'--ks-pass','env:ANDROID_KEYSTORE_PASSWORD','--key-pass','env:ANDROID_KEYSTORE_PASSWORD','--out',final,out/'aligned.apk'])
run([tools/'apksigner','verify','--verbose',final]);print('APK:',final)
