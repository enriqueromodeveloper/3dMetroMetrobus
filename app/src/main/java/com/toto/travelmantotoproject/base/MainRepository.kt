package com.toto.travelmantotoproject.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainRepository @Inject  constructor(val retrofitService: RetrofitService) {

    suspend fun getPokemon(){
        CoroutineScope(Dispatchers.Unconfined).launch {

        }
    }

}