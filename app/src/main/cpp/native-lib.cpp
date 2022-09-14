#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_fi_purefun_androidprotobufpinger_PingerRCP_stringFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jstring txt) {
    const char *str = env->GetStringUTFChars(txt, 0);
    std::string hello = "JNI: ";
    hello.append(str);
    return env->NewStringUTF(hello.c_str());
}
