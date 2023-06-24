#include "danilo_jovanovic_shoppinglist_JNIExample.h"

JNIEXPORT jint JNICALL Java_danilo_jovanovic_shoppinglist_JNIExample_increment
  (JNIEnv * env, jobject jobj, jint x){
    return ++x;
  }