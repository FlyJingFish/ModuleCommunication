package com.flyjingfish.user

import android.os.Parcel
import android.os.Parcelable
import com.flyjingfish.module_communication_annotation.ExposeBean
import java.io.Serializable
@ExposeBean
data class TestBean2(val test:Int): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(test)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TestBean2> {
        override fun createFromParcel(parcel: Parcel): TestBean2 {
            return TestBean2(parcel)
        }

        override fun newArray(size: Int): Array<TestBean2?> {
            return arrayOfNulls(size)
        }
    }
}