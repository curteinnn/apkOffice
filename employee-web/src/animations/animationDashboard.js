import gsap from "gsap";

export function animationDashboard() {
  gsap.fromTo(
    ".card-dashboard",
    {
      opacity: 0,
      delay: 0.5,
    },
    { opacity: 1 },
  );
}
