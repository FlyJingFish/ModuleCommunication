package com.flyjingfish.module_communication_route.lost

interface RouterLost {
    fun order(): Int
    fun onLost(lostPoint: LostPoint)
}