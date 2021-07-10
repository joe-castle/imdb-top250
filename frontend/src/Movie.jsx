import React from 'react'
import classNames from 'classnames'
import useSWR from 'swr'

import styles from './Movie.module.css'

function Movie({movie}) {
  const {data: user, mutate} = useSWR("/api/v1/user")

  function isMovieWatched(title) {
    return user?.watchedList.some(movie => movie.title === title && movie.watched)
  }

  function updateMovieWatchedStatus(title) {
    if (user?.name) {
      // mutate({
      //   name: user.name,
      //   watchedList: [
      //     ...user.watchedList,
      //
      //   ]
      // })
      mutate(async () => {
        return await fetch(`/api/v1/user/movie/${title}`, {method: 'POST'}).then(res => res.json())
      })
    }
  }

  return (
    <div className={classNames("col-sm-8 offset-sm-2", {
      [styles.movie]: true,
      [styles.watched]: isMovieWatched(movie.title)
    })}
         id={movie.title}
         onClick={() => updateMovieWatchedStatus(movie.title)}>
      <div className={styles.watchedTick}>
        <span>✓</span>
      </div>
      <div className="row">
        <div className={`col-3 ${styles.pl}`}>
          <img src={movie.posterUrl} className="img-fluid d-block mx-auto mx-md-0"
               alt={`Movie poster for ${movie.title}`}/>
        </div>
        <div className={`col-9 ${styles.pl}`}>
          <div className="mt-3">
            <span>{movie.position}. </span>
            <a href={movie.imdbUrl}>{movie.title}</a>
            <span className={styles.year}> ({movie.year})</span>
            <div className={styles.subtitle}>
              <span>{movie.certificate}</span>
              <span> | </span>
              <span>{movie.time}</span>
              <span> | </span>
              <span>{movie.genre}</span>
            </div>
          </div>
          <div className="mt-3">
            <strong>{movie.rating}</strong>
          </div>
          <span className="oi oi-check"/>
          <div className="mt-3">
            <p>{movie.description}</p>
          </div>
          <div>
            <span>Director: </span><a href={movie.director.url}>{movie.director.name}</a>
            <span> | </span>
            <span>Stars: </span>
            {movie.stars.map((star, index, arr) => (
              <span key={index}>
                <a href={star.url}>{star.name}</a>
                {index !== arr.length - 1 && <span>, </span>}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Movie