@file:Suppress("UNCHECKED_CAST", "INACCESSIBLE_TYPE", "unused")

package ru.sudox.android.core.realm

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.realm.DynamicRealm
import io.realm.DynamicRealmObject
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.internal.RealmObjectProxy
import io.realm.rx.ObjectChange
import ru.sudox.android.core.realm.support.RealmRxJava3ObservableFactory

private val realmObservableFactory = RealmRxJava3ObservableFactory()

fun <E : RealmModel> RealmObject.toFlowable(): Flowable<E> {
    return toFlowable(this as E)
}

fun <E : RealmModel> RealmObject.toChangesetObservable(): Observable<ObjectChange<E>>  {
    return toChangesetObservable(this as E)
}

fun <E : RealmModel> RealmObject.toFlowable(obj: E): Flowable<E> {
    if (obj is RealmObjectProxy) {
        val realm = obj.`realmGet$proxyState`().`realm$realm`

        return if (realm is Realm) {
            realmObservableFactory.from(realm, obj)
        } else if (realm is DynamicRealm) {
            realmObservableFactory.from(realm, obj as DynamicRealmObject) as Flowable<E>
        } else {
            throw UnsupportedOperationException("${realm.javaClass} does not support RxJava. See https://realm.io/docs/java/latest/#rxjava for more details.")
        }
    } else {
        throw IllegalArgumentException("Cannot create Observables from unmanaged RealmObjects")
    }
}

fun <E : RealmModel> RealmObject.toChangesetObservable(obj: E): Observable<ObjectChange<E>> {
    if (obj is RealmObjectProxy) {
        val realm = obj.`realmGet$proxyState`().`realm$realm`

        return if (realm is Realm) {
            realmObservableFactory.changesetsFrom(realm, obj)
        } else if (realm is DynamicRealm) {
            realmObservableFactory.changesetsFrom(realm, obj as DynamicRealmObject) as Observable<ObjectChange<E>>
        } else {
            throw UnsupportedOperationException("${realm.javaClass} does not support RxJava. See https://realm.io/docs/java/latest/#rxjava for more details.")
        }
    } else {
        throw IllegalArgumentException("Cannot create Observables from unmanaged RealmObjects")
    }
}