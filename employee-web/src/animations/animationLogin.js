import gsap from "gsap";


export function animationLogin() {
  gsap.from(".form", {
    y: 10,
    opacity: 0,
    duration: 0.5,
  });

  gsap.from(".logo",{
    opacity:0,
    ease:"power1.inOut",
    duration:1,
  })
}
