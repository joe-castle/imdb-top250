import React from 'react'
import useSWR from 'swr'
import classNames from 'classnames'

import styles from './Controls.module.css'

function Controls({genres, updateGenreFilter, genreFilter}) {
  const {data: user} = useSWR("/api/v1/user")

  return (
    <div className="row mb-3">
      <div className="col-sm-12 col-md-8 offset-md-2">
        {!!user && <>
            <div>
              <strong>Watched: </strong>
              <span id="watchedCount">{(user.watchedList?.length) || 0}</span>
              <span> / </span>
              <span>250</span>
            </div>
              <div className="mt-2">
              <span id="hide-watched" className="btn btn-outline-primary">Hide Watched</span>
            </div>
          </>
        }
        <div className="mt-4">
          <input id="search" className="form-control" type="text" placeholder="Enter movie title to search..."/>
        </div>
        <div className="mt-2">
          {genres?.map(genre => (
            <button
              key={genre}
              className={classNames(`btn btn-outline-info ${styles.filter}`, {active: genreFilter.includes(genre)})}
              onClick={() => updateGenreFilter(genre)}
            >
              {genre}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Controls