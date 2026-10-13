export default function listVisit() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-linear-to-br from-blue-300 to-white p-4">
      <section className="w-full max-w-5xl p-6">
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="title text-2xl px-1 font-bold text-gray-900 sm:text-3xl ">
              LIST CUSTOMER
            </h1>

            <div className="card-visit mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-2 ">
              <button
                type="button"
                // onClick={() => navigate("/absenMasuk")}
                className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
              >
                <h2 className="text-lg font-semibold text-blue-950">
                  Absen Masuk
                </h2>

                <p className="mt-2 text-sm text-blue-800">
                  Lorem ipsum dolor sit amet consectetur adipisicing elit.
                  Beatae molestiae recusandae deserunt id officiis hic vitae
                  dolorum! Soluta inventore eligendi, voluptatibus
                </p>
              </button>
            </div>
          </div>
        </header>
      </section>
    </main>
  );
}
