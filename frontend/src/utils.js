export async function fetcher(url, method = 'GET') {
  const res = await fetch(url, { method })

  // If the status code is not in the range 200-299,
  // we still try to parse and throw it.
  if (!res.ok) {
    const error = new Error('An error occurred while fetching the data.')
    // Attach extra info to the error object.
    error.info = await res.json()
    error.status = res.status
    throw error
  }

  return res.json()
}

export function isMovieWatched(watchedList, title) {
  return watchedList?.some(movie => movie.title === title && movie.watched === true)
}