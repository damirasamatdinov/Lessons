precision mediump float;

uniform vec3 u_LightPos;

varying vec3 v_Position;
varying vec3 v_Normal;
varying vec4 v_Color;

void main() {

    float distanse = length(u_LightPos - v_Position);

    vec3 lightVertor = normalize(u_LightPos - v_Position);

    float diffuse;
    if(gl_FrontFacing){
        diffuse = max(dot(v_Normal,lightVertor),0.0);
    }else{
        diffuse = max(dot(-v_Normal,lightVertor),0.0);
    }

    diffuse = diffuse * (1.0 /(1.0+ (0.10 *  distanse)));

    diffuse = diffuse+0.3;

    gl_FragColor = (v_Color * diffuse);
}
