package com.example.mobv.data.db.model


data class Pubs(
    val version: Float,
    val generator: String,
    val osm3s: OSM3S,
    val elements: ArrayList<Pub>
) {
    data class OSM3S(
        val timestamp_osm_base: String,
        val copyright: String
    )
}