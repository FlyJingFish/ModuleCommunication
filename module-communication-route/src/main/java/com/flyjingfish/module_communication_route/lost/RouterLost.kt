package com.flyjingfish.module_communication_route.lost

interface RouterLost {
    fun onLost(lostPoint: LostPoint)
    fun order(): Int
}