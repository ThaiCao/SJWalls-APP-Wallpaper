/*
 * Copyright (c) 2017. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.frames.ui.fragments.base

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import jahirfiquitiva.libs.frames.data.models.Collection
import jahirfiquitiva.libs.frames.data.models.Wallpaper
import jahirfiquitiva.libs.frames.providers.viewmodels.CollectionsViewModel
import jahirfiquitiva.libs.frames.providers.viewmodels.WallpapersViewModel

abstract class BaseFramesFragment<in T, in VH:RecyclerView.ViewHolder>:BaseDatabaseFragment<T, VH>() {
    
    internal var wallpapersModel:WallpapersViewModel? = null
    internal var collectionsModel:CollectionsViewModel? = null
    
    override fun initViewModel() {
        super.initViewModel()
        if (wallpapersModel == null)
            wallpapersModel = ViewModelProviders.of(activity).get(WallpapersViewModel::class.java)
        if (collectionsModel == null)
            collectionsModel = ViewModelProviders.of(activity).get(CollectionsViewModel::class.java)
    }
    
    override fun registerObserver() {
        super.registerObserver()
        wallpapersModel?.items?.observe(this, Observer { data ->
            data?.let { doOnWallpapersChange(it, fromCollectionActivity()) }
        })
        collectionsModel?.items?.observe(this, Observer { data ->
            data?.let { doOnCollectionsChange(it) }
        })
    }
    
    override fun loadDataFromViewModel() {
        super.loadDataFromViewModel()
        if (!fromCollectionActivity()) wallpapersModel?.loadData(context)
    }
    
    override fun unregisterObserver() {
        super.unregisterObserver()
        wallpapersModel?.items?.removeObservers(this)
        collectionsModel?.items?.removeObservers(this)
        collectionsModel?.stopTask(true)
        wallpapersModel?.stopTask(true)
    }
    
    open fun doOnCollectionsChange(data:ArrayList<Collection>) {}
    
    override fun doOnWallpapersChange(data:ArrayList<Wallpaper>, fromCollectionActivity:Boolean) {
        super.doOnWallpapersChange(data, fromCollectionActivity)
        if (!fromCollectionActivity) collectionsModel?.loadData(data)
    }
    
    open fun reloadData(section:Int) {
        when (section) {
            0, 1 -> {
                wallpapersModel?.stopTask(true)
                wallpapersModel?.loadData(context, true)
            }
            2 -> {
                favoritesModel?.stopTask(true)
                getDatabase()?.let { favoritesModel?.loadData(it, true) }
            }
        }
    }
    
    abstract fun applyFilter(filter:String)
    abstract fun scrollToTop()
}